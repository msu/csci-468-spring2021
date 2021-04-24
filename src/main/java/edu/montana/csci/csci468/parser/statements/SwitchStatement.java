package edu.montana.csci.csci468.parser.statements;

import edu.montana.csci.csci468.bytecode.ByteCodeGenerator;
import edu.montana.csci.csci468.eval.BreakExeption;
import edu.montana.csci.csci468.eval.CatscriptRuntime;
import edu.montana.csci.csci468.eval.ContinueException;
import edu.montana.csci.csci468.parser.CatscriptType;
import edu.montana.csci.csci468.parser.ErrorType;
import edu.montana.csci.csci468.parser.SymbolTable;
import edu.montana.csci.csci468.parser.expressions.Expression;
import edu.montana.csci.csci468.parser.expressions.IntegerLiteralExpression;
import edu.montana.csci.csci468.parser.expressions.StringLiteralExpression;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class SwitchStatement extends Statement {
    private Expression expression;
    private List<Statement> isStatements = Collections.emptyList();
    private CatscriptType type;

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = addChild(expression);
    }

    public void setIsStatements(List<Statement> statements) {
        this.isStatements = new LinkedList<>();
        for (Statement statement : statements) {
            this.isStatements.add(addChild(statement));
        }
    }


    @Override
    public void validate(SymbolTable symbolTable) {
        expression.validate(symbolTable);
        if (!expression.getType().equals(CatscriptType.INT) && !expression.getType().equals(CatscriptType.STRING)) {
            expression.addError(ErrorType.INCOMPATIBLE_TYPES);
        }
        symbolTable.pushScope();
        for (Statement isStatement : isStatements) {
            isStatement.validate(symbolTable);
        }
        symbolTable.popScope();
    }

    //==============================================================
    // Implementation
    //==============================================================
    @Override
    public void execute(CatscriptRuntime runtime) {
        runtime.pushScope();
        Object o = expression.evaluate(runtime);
        if (o != null) {
            boolean def = true;
            for (int i = 0; i < isStatements.size(); i++) {
                if (i == isStatements.size() - 1 && ((IsStatement) isStatements.get(i)).isDefault() && def) {
                    isStatements.get(i).execute(runtime);
                    break;
                }
                Object x = ((IsStatement) isStatements.get(i)).getExpression().evaluate(runtime);
                if (x.equals(o)) {
                    try {
                        def = false;
                        isStatements.get(i).execute(runtime);
                    } catch (BreakExeption b) {
                        break;
                    } catch (ContinueException c) {
                        if (i < isStatements.size() - 1) {
                            isStatements.get(i + 1).execute(runtime);
                            i+= 2;
                        }
                    }
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

    }

    public CatscriptType getType() {
        return type;
    }

    public void setType(CatscriptType type) {
        this.type = type;
    }
}
