package edu.montana.csci.csci468.parser.statements;

import edu.montana.csci.csci468.bytecode.ByteCodeGenerator;
import edu.montana.csci.csci468.eval.CatscriptRuntime;
import edu.montana.csci.csci468.parser.CatscriptType;
import edu.montana.csci.csci468.parser.SymbolTable;
import edu.montana.csci.csci468.parser.expressions.Expression;
import org.objectweb.asm.Opcodes;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static edu.montana.csci.csci468.bytecode.ByteCodeGenerator.internalNameFor;

public class CatScriptProgram extends Statement {

    private StringBuffer output = new StringBuffer();
    private List<Statement> statements = new LinkedList<>();
    private Map<String, FunctionDefinitionStatement> functions = new HashMap<>();
    private Expression expression;

    public void print(Object v) {
        output.append(v).append("\n");
    }

    public String getOutput() {
        return output.toString();
    }

    public void addStatement(Statement child) {
        Statement statement = addChild(child);
        statements.add(statement);
        if (statement instanceof FunctionDefinitionStatement) {
            FunctionDefinitionStatement function = (FunctionDefinitionStatement) statement;
            functions.put(function.getName(), function);
        }
    }

    public void setExpression(Expression expression) {
        this.expression = addChild(expression);
    }

    public Expression getExpression() {
        return expression;
    }

    public List<Statement> getStatements() {
        return statements;
    }

    public boolean isExpression() {
        return expression != null;
    }

    public FunctionDefinitionStatement getFunction(String name) {
        return functions.get(name);
    }

    @Override
    public void validate(SymbolTable symbolTable) {
        if (expression != null) {
            expression.validate(symbolTable);
        } else {
            for (Statement statement : statements) {
                statement.validate(symbolTable);
            }
        }
    }

    public void execute() {
        execute(new CatscriptRuntime());
    }

    //==============================================================
    // Implementation
    //==============================================================
    @Override
    public void execute(CatscriptRuntime runtime) {
        if (expression != null) {
            print(expression.evaluate(runtime));
        } else {
            for (Statement statement : statements) {
                statement.execute(runtime);
            }
        }
    }

    @Override
    public void transpile(StringBuilder javascript) {
        if (isExpression()) {
            javascript.append("print(");
            expression.transpile(javascript);
            javascript.append(");\n");
        } else {
            for (Statement statement : statements) {
                statement.transpile(javascript);
            }
        }
    }

    @Override
    public void compile(ByteCodeGenerator code) {
        if (isExpression()) {
            code.addVarInstruction(Opcodes.ALOAD, 0);
            getExpression().compile(code);
            box(code, getExpression().getType());
            code.addMethodInstruction(Opcodes.INVOKEVIRTUAL, internalNameFor(CatScriptProgram.class),
                    "print", "(Ljava/lang/Object;)V");
            code.addInstruction(Opcodes.RETURN);
        } else {
            for (Statement statement : statements) {
                statement.compile(code);
            }
            code.addInstruction(Opcodes.RETURN);
        }
    }


}
