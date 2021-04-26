package edu.montana.csci.csci468.parser.statements;

import edu.montana.csci.csci468.bytecode.ByteCodeGenerator;
import edu.montana.csci.csci468.eval.CatscriptRuntime;
import edu.montana.csci.csci468.parser.CatscriptType;
import edu.montana.csci.csci468.parser.ErrorType;
import edu.montana.csci.csci468.parser.ParseError;
import edu.montana.csci.csci468.parser.SymbolTable;
import edu.montana.csci.csci468.parser.expressions.Expression;

import java.util.ArrayList;

public class AssignmentStatement extends Statement {
    private Expression expression;
    private String variableName;
    private ArrayList<Expression> arrayIndex;

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = addChild(expression);
    }

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
        boolean bool = false;
        int x;
        x = (bool) ? 1 : 0;
    }

    @Override
    public void validate(SymbolTable symbolTable) {
        expression.validate(symbolTable);
        CatscriptType symbolType = symbolTable.getSymbolType(getVariableName());
        if (symbolType == null) {
            addError(ErrorType.UNKNOWN_NAME);
        } else {
            if (symbolTable.getSymbolType(variableName) != expression.getType() && arrayIndex == null) {
                addError(ErrorType.INCOMPATIBLE_TYPES);
            }
        }
    }

    //==============================================================
    // Implementation
    //==============================================================
    @Override
    public void execute(CatscriptRuntime runtime) {
        if (arrayIndex == null) {
            runtime.setValue(variableName, expression.evaluate(runtime));
        } else {
            ArrayList<Object> array = (ArrayList<Object>) runtime.getValue(variableName);
            for (int i = 0; i < arrayIndex.size() - 1; i++) {
                array = (ArrayList<Object>) array.get((Integer) arrayIndex.get(i).evaluate(runtime));
            }
            array.set((Integer) arrayIndex.get(arrayIndex.size() - 1).evaluate(runtime), expression.evaluate(runtime));
        }
    }

    @Override
    public void transpile(StringBuilder javascript) {
        super.transpile(javascript);
    }

    @Override
    public void compile(ByteCodeGenerator code) {
        super.compile(code);
    }

    public void setArrayIndex(ArrayList<Expression> arrayIndex) {
        this.arrayIndex = arrayIndex;
    }
}
