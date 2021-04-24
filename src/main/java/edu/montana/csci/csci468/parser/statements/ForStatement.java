package edu.montana.csci.csci468.parser.statements;

import edu.montana.csci.csci468.bytecode.ByteCodeGenerator;
import edu.montana.csci.csci468.eval.BreakExeption;
import edu.montana.csci.csci468.eval.CatscriptRuntime;
import edu.montana.csci.csci468.eval.ContinueException;
import edu.montana.csci.csci468.parser.CatscriptType;
import edu.montana.csci.csci468.parser.ErrorType;
import edu.montana.csci.csci468.parser.ParseError;
import edu.montana.csci.csci468.parser.SymbolTable;
import edu.montana.csci.csci468.parser.expressions.Expression;
import edu.montana.csci.csci468.parser.expressions.IntegerLiteralExpression;
import edu.montana.csci.csci468.parser.expressions.RangeExpression;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

import javax.naming.ldap.Control;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import static edu.montana.csci.csci468.bytecode.ByteCodeGenerator.internalNameFor;

public class ForStatement extends Statement {
    private Expression expression;
    private String variableName;
    private List<Statement> body;

    public void setExpression(Expression expression) {
        this.expression = addChild(expression);
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public void setBody(List<Statement> statements) {
        this.body = new LinkedList<>();
        for (Statement statement : statements) {
            this.body.add(addChild(statement));
        }
    }

    public Expression getExpression() {
        return expression;
    }

    public String getVariableName() {
        return variableName;
    }

    public List<Statement> getBody() {
        return body;
    }

    @Override
    public void validate(SymbolTable symbolTable) {
        symbolTable.pushScope();
        if (symbolTable.hasSymbol(variableName)) {
            addError(ErrorType.DUPLICATE_NAME);
        } else {
            expression.validate(symbolTable);
            CatscriptType type = expression.getType();
            if (expression instanceof RangeExpression) {
                symbolTable.registerSymbol(variableName, CatscriptType.INT);
            } else {
                if (type instanceof CatscriptType.ListType) {
                    symbolTable.registerSymbol(variableName, getComponentType());
                } else {
                    addError(ErrorType.INCOMPATIBLE_TYPES, getStart());
                    symbolTable.registerSymbol(variableName, CatscriptType.OBJECT);
                }
            }
        }
        for (Statement statement : body) {
            statement.validate(symbolTable);
        }

        symbolTable.popScope();
    }

    private CatscriptType getComponentType() {
        if (expression instanceof IntegerLiteralExpression) {
            return CatscriptType.INT;
        }
        return ((CatscriptType.ListType) expression.getType()).getComponentType();
    }

    //==============================================================
    // Implementation
    //==============================================================
    @Override
    public void execute(CatscriptRuntime runtime) {
        runtime.pushScope();
        if (expression instanceof RangeExpression) {
            List<Integer> control = (List<Integer>) expression.evaluate(runtime);
            Integer start = 0, stop = control.get(0), step = 1;
            if (control.size() > 1) {
                start = control.get(0);
                stop = control.get(1);
                if (control.size() == 3) {
                    step = control.get(2);
                }
            }
            for (int x = start; x < stop; x += step) {
                try {
                    runtime.setValue(variableName, x);
                    for (Statement statement : body) {
                        statement.execute(runtime);
                    }
                } catch (BreakExeption b) {
                    break;
                } catch (ContinueException ignored) {

                }
            }
        } else {
            List values = (List) expression.evaluate(runtime);
            for (Object value : values) {
                try {
                    runtime.setValue(variableName, value);
                    for (Statement statement : body) {
                        statement.execute(runtime);
                    }
                } catch (BreakExeption b) {
                    break;
                } catch (ContinueException ignored) {

                }
            }
        }
        runtime.popScope();
    }

    @Override
    public void transpile(StringBuilder javascript) {
        super.transpile(javascript);
    }

    @Override
    public void compile(ByteCodeGenerator code) {
        Integer iterslot = code.nextLocalStorageSlot();

        Label iterationStart = new Label();
        Label end = new Label();

        expression.compile(code);

        code.addMethodInstruction(Opcodes.INVOKEINTERFACE, internalNameFor(List.class), "iterator", "()Ljava/util/Iterator;");
        code.addVarInstruction(Opcodes.ASTORE, iterslot);
        code.addLabel(iterationStart);

        code.addVarInstruction(Opcodes.ALOAD, iterslot);
        code.addMethodInstruction(Opcodes.INVOKEINTERFACE, internalNameFor(Iterator.class), "hasNext", "()Z");
        code.addJumpInstruction(Opcodes.IFEQ, end);

        CatscriptType componentType = getComponentType();
        code.addVarInstruction(Opcodes.ALOAD, iterslot);
        code.addMethodInstruction(Opcodes.INVOKEINTERFACE, internalNameFor(Iterator.class), "next", "()Ljava/lang/Object;");
        code.addTypeInstruction(Opcodes.CHECKCAST, internalNameFor(componentType.getJavaType()));
        unbox(code, componentType);

        Integer iteratorvariableslot = code.createLocalStorageSlotFor(variableName);
        if (componentType.equals(CatscriptType.INT) || componentType.equals(CatscriptType.BOOLEAN)) {
            code.addVarInstruction(Opcodes.ISTORE, iteratorvariableslot);
        } else {
            code.addVarInstruction(Opcodes.ASTORE, iteratorvariableslot);
        }

        for (Statement statement : body) {
            statement.compile(code);
        }

        code.addJumpInstruction(Opcodes.GOTO, iterationStart);
        code.addLabel(end);
    }

}
