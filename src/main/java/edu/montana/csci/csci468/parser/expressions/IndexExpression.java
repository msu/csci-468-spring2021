package edu.montana.csci.csci468.parser.expressions;

import edu.montana.csci.csci468.bytecode.ByteCodeGenerator;
import edu.montana.csci.csci468.eval.CatscriptRuntime;
import edu.montana.csci.csci468.parser.CatscriptType;
import edu.montana.csci.csci468.parser.SymbolTable;

import java.util.ArrayList;

public class IndexExpression extends Expression {
    private String variableName;
    private ArrayList<Expression> indexes;

    @Override
    public void validate(SymbolTable symbolTable) {
        for (Expression index : indexes) {
            index.validate(symbolTable);
        }
    }

    @Override
    public CatscriptType getType() {
        return indexes.get(0).getType();
    }

    //==============================================================
    // Implementation
    //==============================================================

    @Override
    public Object evaluate(CatscriptRuntime runtime) {
        ArrayList<Object> array = (ArrayList<Object>) runtime.getValue(variableName);
        for (int i = 0; i < indexes.size() - 1; i++) {
            array = (ArrayList<Object>) array.get((Integer) indexes.get(i).evaluate(runtime));
        }
        int index = (Integer) indexes.get(indexes.size() - 1).evaluate(runtime);
        return array.get(index + ((index < 0) ? array.size() : 0));
    }

    @Override
    public void transpile(StringBuilder javascript) {
    }

    @Override
    public void compile(ByteCodeGenerator code) {

    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public void setIndexes(ArrayList<Expression> indexes) {
        this.indexes = indexes;
    }
}
