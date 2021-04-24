package edu.montana.csci.csci468.parser.expressions;

import edu.montana.csci.csci468.bytecode.ByteCodeGenerator;
import edu.montana.csci.csci468.eval.CatscriptRuntime;
import edu.montana.csci.csci468.parser.CatscriptType;
import edu.montana.csci.csci468.parser.ErrorType;
import edu.montana.csci.csci468.parser.SymbolTable;

import java.util.ArrayList;
import java.util.List;

public class RangeExpression extends Expression {

    private List<Expression> control;

    @Override
    public void validate(SymbolTable symbolTable) {
        if (control.size() < 1 || control.size() > 3) {
            addError(ErrorType.UNEXPECTED_TOKEN);
        }
    }

    @Override
    public CatscriptType getType() {
        return CatscriptType.INT;
    }

    //==============================================================
    // Implementation
    //==============================================================

    @Override
    public Object evaluate(CatscriptRuntime runtime) {
        List<Integer> ints = new ArrayList<>();
        for (Expression expression : control) {
            ints.add((Integer) expression.evaluate(runtime));
        }
        return ints;
    }

    @Override
    public void transpile(StringBuilder javascript) {

    }

    @Override
    public void compile(ByteCodeGenerator code) {

    }


    public void setControl(List<Expression> control) {
        this.control = control;
    }
}
