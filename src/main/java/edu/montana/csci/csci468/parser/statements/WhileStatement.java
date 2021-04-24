package edu.montana.csci.csci468.parser.statements;

import edu.montana.csci.csci468.bytecode.ByteCodeGenerator;
import edu.montana.csci.csci468.eval.BreakExeption;
import edu.montana.csci.csci468.eval.CatscriptRuntime;
import edu.montana.csci.csci468.eval.ContinueException;
import edu.montana.csci.csci468.parser.CatscriptType;
import edu.montana.csci.csci468.parser.ErrorType;
import edu.montana.csci.csci468.parser.SymbolTable;
import edu.montana.csci.csci468.parser.expressions.BooleanLiteralExpression;
import edu.montana.csci.csci468.parser.expressions.ComparisonExpression;
import edu.montana.csci.csci468.parser.expressions.EqualityExpression;
import edu.montana.csci.csci468.parser.expressions.Expression;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static edu.montana.csci.csci468.bytecode.ByteCodeGenerator.internalNameFor;

public class WhileStatement extends Statement {
    private Expression expression;
    private List<Statement> body;

    public void setExpression(Expression expression) {
        this.expression = addChild(expression);
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

    public List<Statement> getBody() {
        return body;
    }

    @Override
    public void validate(SymbolTable symbolTable) {
        symbolTable.pushScope();
        expression.validate(symbolTable);
        if (!(expression instanceof EqualityExpression ||
                expression instanceof BooleanLiteralExpression ||
                expression instanceof ComparisonExpression)) {
            addError(ErrorType.INCOMPATIBLE_TYPES);
        }

        for (Statement statement : body) {
            statement.validate(symbolTable);
        }

        symbolTable.popScope();
    }

    private CatscriptType getComponentType() {
        return ((CatscriptType.ListType) expression.getType()).getComponentType();
    }

    //==============================================================
    // Implementation
    //==============================================================
    @Override
    public void execute(CatscriptRuntime runtime) {
        runtime.pushScope();
        while ((Boolean) expression.evaluate(runtime)) {
            try {
                for (Statement statement : body) {
                    statement.execute(runtime);
                }
            } catch (BreakExeption b) {
                break;
            } catch (ContinueException ignored) {
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
        super.compile(code);
    }

}
