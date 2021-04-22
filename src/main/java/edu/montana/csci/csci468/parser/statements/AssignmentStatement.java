package edu.montana.csci.csci468.parser.statements;

import edu.montana.csci.csci468.bytecode.ByteCodeGenerator;
import edu.montana.csci.csci468.eval.CatscriptRuntime;
import edu.montana.csci.csci468.parser.CatscriptType;
import edu.montana.csci.csci468.parser.ErrorType;
import edu.montana.csci.csci468.parser.ParseError;
import edu.montana.csci.csci468.parser.SymbolTable;
import edu.montana.csci.csci468.parser.expressions.Expression;
import org.objectweb.asm.Opcodes;

public class AssignmentStatement extends Statement {
    private Expression expression;
    private String variableName;

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
    }

    @Override
    public void validate(SymbolTable symbolTable) {
        expression.validate(symbolTable);
        CatscriptType symbolType = symbolTable.getSymbolType(getVariableName());
        if (symbolType == null) {
            addError(ErrorType.UNKNOWN_NAME);
        } else {
            if (symbolTable.getSymbolType(variableName) != expression.getType()) {
                addError(ErrorType.INCOMPATIBLE_TYPES);
            }
        }
    }

    //==============================================================
    // Implementation
    //==============================================================
    @Override
    public void execute(CatscriptRuntime runtime) {
        runtime.setValue(variableName,expression.evaluate(runtime));
    }

    @Override
    public void transpile(StringBuilder javascript) {
        super.transpile(javascript);
    }

    @Override
    public void compile(ByteCodeGenerator code) {
        expression.compile(code);
        Integer slotforvar = code.createLocalStorageSlotFor(getVariableName());
        if (expression.getType() == CatscriptType.INT || expression.getType() == CatscriptType.BOOLEAN) {
            code.addVarInstruction(Opcodes.ISTORE, slotforvar);
        } else {
            code.addVarInstruction(Opcodes.ASTORE, slotforvar);
        }
    }
}
