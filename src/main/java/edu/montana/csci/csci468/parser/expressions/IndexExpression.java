package edu.montana.csci.csci468.parser.expressions;

import edu.montana.csci.csci468.bytecode.ByteCodeGenerator;
import edu.montana.csci.csci468.eval.CatscriptRuntime;
import edu.montana.csci.csci468.parser.CatscriptType;
import edu.montana.csci.csci468.parser.SymbolTable;

import java.util.ArrayList;
import java.util.List;

public class IndexExpression extends Expression {
    private String variableName;
    private Expression expression;

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
        ArrayList list = (ArrayList) runtime.getValue(variableName);
        return list.get((Integer) expression.evaluate(runtime));
    }

    @Override
    public void transpile(StringBuilder javascript) {
        javascript.append("(");
        expression.transpile(javascript);
        javascript.append(")");
    }

    @Override
    public void compile(ByteCodeGenerator code) {

    }

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }
}