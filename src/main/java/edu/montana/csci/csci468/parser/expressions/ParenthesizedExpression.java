package edu.montana.csci.csci468.parser.expressions;

import edu.montana.csci.csci468.bytecode.ByteCodeGenerator;
import edu.montana.csci.csci468.eval.CatscriptRuntime;
import edu.montana.csci.csci468.parser.CatscriptType;
import edu.montana.csci.csci468.parser.ParseError;
import edu.montana.csci.csci468.parser.SymbolTable;
import edu.montana.csci.csci468.tokenizer.TokenType;

public class ParenthesizedExpression extends Expression {

    private final Expression expression;

    public ParenthesizedExpression(Expression expression) {
        this.expression = addChild(expression);
    }

    public Expression getExpression() {
        return expression;
    }

    @Override
    public void validate(SymbolTable symbolTable) {
        expression.validate(symbolTable);
    }

    @Override
    public CatscriptType getType() {
        return expression.getType();
    }

    //==============================================================
    // Implementation
    //==============================================================

    @Override
    public Object evaluate(CatscriptRuntime runtime) {
        return expression.evaluate(runtime);
    }

    @Override
    public void transpile(StringBuilder javascript) {
        javascript.append("(");
        expression.transpile(javascript);
        javascript.append(")");
    }

    @Override
    public void compile(ByteCodeGenerator code) {
        expression.compile(code);
    }

}
