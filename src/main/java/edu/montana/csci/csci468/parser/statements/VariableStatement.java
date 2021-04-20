package edu.montana.csci.csci468.parser.statements;

import edu.montana.csci.csci468.bytecode.ByteCodeGenerator;
import edu.montana.csci.csci468.eval.CatscriptRuntime;
import edu.montana.csci.csci468.parser.CatscriptType;
import edu.montana.csci.csci468.parser.ErrorType;
import edu.montana.csci.csci468.parser.ParseError;
import edu.montana.csci.csci468.parser.SymbolTable;
import edu.montana.csci.csci468.parser.expressions.Expression;
import org.eclipse.jetty.websocket.common.OpCode;
import org.objectweb.asm.Opcodes;

public class VariableStatement extends Statement {
    private Expression expression;
    private String variableName;
    private CatscriptType explicitType;
    private CatscriptType type;

    public Expression getExpression() {
        return expression;
    }

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public void setExpression(Expression parseExpression) {
        this.expression = addChild(parseExpression);
    }

    public void setExplicitType(CatscriptType type) {
        this.explicitType = type;
    }

    public CatscriptType getExplicitType() {
        return explicitType;
    }

    public boolean isGlobal() {
        return getParent() instanceof CatScriptProgram;
    }

    @Override
    public void validate(SymbolTable symbolTable) {
        expression.validate(symbolTable);
        if (symbolTable.hasSymbol(variableName)) {
            addError(ErrorType.DUPLICATE_NAME);
        } else {
            if (explicitType == null) {
                type = expression.getType();
            } else if (!explicitType.isAssignableFrom(expression.getType())) {
                addError(ErrorType.INCOMPATIBLE_TYPES);
            }
            symbolTable.registerSymbol(variableName, type);
        }
    }

    public CatscriptType getType() {
        return type;
    }

    //==============================================================
    // Implementation
    //==============================================================
    @Override
    public void execute(CatscriptRuntime runtime) {
        runtime.setValue(variableName, expression.evaluate(runtime));
    }

    @Override
    public void transpile(StringBuilder javascript) {
        super.transpile(javascript);
    }

    @Override
    public void compile(ByteCodeGenerator code) {
        if (isGlobal()) {
            code.addVarInstruction(Opcodes.ALOAD, 0);
            expression.compile(code);
            if (getType() == CatscriptType.INT) {
                code.addField(getVariableName(), "I");
                code.addFieldInstruction(Opcodes.PUTFIELD, getVariableName(), "I", code.getProgramInternalName());
            } else if (getType() == CatscriptType.BOOLEAN) {
                code.addField(getVariableName(), "Z");
                code.addFieldInstruction(Opcodes.PUTFIELD, getVariableName(), "Z", code.getProgramInternalName());
            } else if (getType() == CatscriptType.NULL) {
                code.addField(getVariableName(), "Ljava/lang/Object;");
                code.addFieldInstruction(Opcodes.PUTFIELD, getVariableName(), "Ljava/lang/Object;", code.getProgramInternalName());
            } else {
                code.addField(getVariableName(), "L" + ByteCodeGenerator.internalNameFor(getType().getJavaType()) + ";");
                code.addFieldInstruction(Opcodes.PUTFIELD, getVariableName(), "L" + ByteCodeGenerator.internalNameFor(getType().getJavaType()) + ";", code.getProgramInternalName());
            }
        } else {
            expression.compile(code);
            Integer slotforvar = code.createLocalStorageSlotFor(getVariableName());
            if (getType() == CatscriptType.INT || getType() == CatscriptType.BOOLEAN) {
                code.addVarInstruction(Opcodes.ISTORE, slotforvar);
            } else {
                code.addVarInstruction(Opcodes.ASTORE, slotforvar);
            }
        }
    }
}
