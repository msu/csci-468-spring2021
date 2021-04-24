package edu.montana.csci.csci468.parser.statements;

import edu.montana.csci.csci468.bytecode.ByteCodeGenerator;
import edu.montana.csci.csci468.eval.CatscriptRuntime;
import edu.montana.csci.csci468.parser.SymbolTable;
import edu.montana.csci.csci468.parser.expressions.Expression;
import edu.montana.csci.csci468.tokenizer.Token;
import edu.montana.csci.csci468.tokenizer.TokenType;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class IsStatement extends Statement {
    private Expression expression;
    private Token token;
    private List<Statement> trueStatements = Collections.emptyList();

    public boolean isDefault() {
        return token.getType().equals(TokenType.DEFAULT);
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = addChild(expression);
    }

    public void setTrueStatements(List<Statement> statements) {
        this.trueStatements = new LinkedList<>();
        for (Statement statement : statements) {
            this.trueStatements.add(addChild(statement));
        }
    }


    @Override
    public void validate(SymbolTable symbolTable) {

    }

    //==============================================================
    // Implementation
    //==============================================================
    @Override
    public void execute(CatscriptRuntime runtime) {
        runtime.pushScope();
        trueStatements.forEach(statement -> statement.execute(runtime));
        runtime.popScope();

    }

    @Override
    public void transpile(StringBuilder javascript) {
        super.transpile(javascript);
    }

    @Override
    public void compile(ByteCodeGenerator code) {

    }

    @Override
    public void setToken(Token token) {
        this.token = token;
    }
}
