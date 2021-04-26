package edu.montana.csci.csci468.parser.expressions;

import edu.montana.csci.csci468.bytecode.ByteCodeGenerator;
import edu.montana.csci.csci468.eval.CatscriptRuntime;
import edu.montana.csci.csci468.parser.CatscriptType;
import edu.montana.csci.csci468.parser.SymbolTable;
import edu.montana.csci.csci468.tokenizer.Token;

public class NullCheckExpression extends Expression {

    private Token variable;


    @Override
    public void validate(SymbolTable symbolTable) {

    }

    @Override
    public CatscriptType getType() {
        return CatscriptType.BOOLEAN;
    }

    //==============================================================
    // Implementation
    //==============================================================

    @Override
    public Object evaluate(CatscriptRuntime runtime) {

        return runtime.getValue(variable.getStringValue()) != null;
    }

    @Override
    public void transpile(StringBuilder javascript) {

    }

    @Override
    public void compile(ByteCodeGenerator code) {

    }

    public void setVariable(Token variable) {
        this.variable = variable;
    }
}
