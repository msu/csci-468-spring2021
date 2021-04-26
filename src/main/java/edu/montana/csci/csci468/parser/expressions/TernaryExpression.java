package edu.montana.csci.csci468.parser.expressions;

import edu.montana.csci.csci468.bytecode.ByteCodeGenerator;
import edu.montana.csci.csci468.eval.CatscriptRuntime;
import edu.montana.csci.csci468.parser.CatscriptType;
import edu.montana.csci.csci468.parser.SymbolTable;

public class TernaryExpression extends Expression {

    private ParenthesizedExpression parenthesizedExpression;
    private Expression trueExpression;
    private Expression falseExpression;


    @Override
    public void validate(SymbolTable symbolTable) {
        parenthesizedExpression.validate(symbolTable);
        trueExpression.validate(symbolTable);
        falseExpression.validate(symbolTable);
    }

    @Override
    public CatscriptType getType() {
        return (trueExpression.getType() == falseExpression.getType()) ? trueExpression.getType() : CatscriptType.OBJECT;
    }

    //==============================================================
    // Implementation
    //==============================================================

    @Override
    public Object evaluate(CatscriptRuntime runtime) {
        //lol
        return ((Boolean) parenthesizedExpression.evaluate(runtime)) ? trueExpression.evaluate(runtime) : falseExpression.evaluate(runtime);
    }

    @Override
    public void transpile(StringBuilder javascript) {

    }

    @Override
    public void compile(ByteCodeGenerator code) {

    }

    public void setParenthesizedExpression(ParenthesizedExpression parenthesizedExpression) {
        this.parenthesizedExpression = parenthesizedExpression;
    }

    public void setTrueExpression(Expression trueExpression) {
        this.trueExpression = trueExpression;
    }

    public void setFalseExpression(Expression falseExpression) {
        this.falseExpression = falseExpression;
    }
}
