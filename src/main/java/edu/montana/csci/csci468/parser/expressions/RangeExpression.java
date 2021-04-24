package edu.montana.csci.csci468.parser.expressions;

import edu.montana.csci.csci468.bytecode.ByteCodeGenerator;
import edu.montana.csci.csci468.eval.CatscriptRuntime;
import edu.montana.csci.csci468.parser.CatscriptType;
import edu.montana.csci.csci468.parser.ErrorType;
import edu.montana.csci.csci468.parser.SymbolTable;

public class RangeExpression extends Expression {

    private Expression expression;

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }

    @Override
    public void validate(SymbolTable symbolTable) {
        if(expression == null){
            addError(ErrorType.INCOMPATIBLE_TYPES);
        }

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
