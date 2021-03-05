package edu.montana.csci.csci468.parser.expressions;

import edu.montana.csci.csci468.bytecode.ByteCodeGenerator;
import edu.montana.csci.csci468.eval.CatscriptRuntime;
import edu.montana.csci.csci468.parser.CatscriptType;
import edu.montana.csci.csci468.parser.ErrorType;
import edu.montana.csci.csci468.parser.ParseError;
import edu.montana.csci.csci468.parser.SymbolTable;
import edu.montana.csci.csci468.tokenizer.Token;
import edu.montana.csci.csci468.tokenizer.TokenType;
import jdk.jfr.Category;
import org.objectweb.asm.Opcodes;

public class AdditiveExpression extends Expression {

    private final Token operator;
    private final Expression leftHandSide;
    private final Expression rightHandSide;

    public AdditiveExpression(Token operator, Expression leftHandSide, Expression rightHandSide) {
        this.leftHandSide = addChild(leftHandSide);
        this.rightHandSide = addChild(rightHandSide);
        this.operator = operator;
    }

    public Expression getLeftHandSide() {
        return leftHandSide;
    }

    public Expression getRightHandSide() {
        return rightHandSide;
    }

    public boolean isAdd() {
        return operator.getType() == TokenType.PLUS;
    }

    @Override
    public void validate(SymbolTable symbolTable) {
        leftHandSide.validate(symbolTable);
        rightHandSide.validate(symbolTable);
        if (getType().equals(CatscriptType.INT)) {
            if (!leftHandSide.getType().equals(CatscriptType.INT)) {
                leftHandSide.addError(ErrorType.INCOMPATIBLE_TYPES);
            }
            if (!rightHandSide.getType().equals(CatscriptType.INT)) {
                rightHandSide.addError(ErrorType.INCOMPATIBLE_TYPES);
            }
        }
        if(getType().equals(CatscriptType.STRING)){
            if(!leftHandSide.getType().equals(CatscriptType.STRING)){
                leftHandSide.addError(ErrorType.INCOMPATIBLE_TYPES);
            }
            if(!rightHandSide.getType().equals(CatscriptType.STRING)){
                rightHandSide.addError(ErrorType.INCOMPATIBLE_TYPES);
            }
        }
    }

    @Override
    public CatscriptType getType() {
        if (leftHandSide.getType().equals(CatscriptType.STRING) || rightHandSide.getType().equals(CatscriptType.STRING)) {
            return CatscriptType.STRING;
        } else {
            return CatscriptType.INT;
        }
    }

    @Override
    public String toString() {
        return super.toString() + "[" + operator.getStringValue() + "]";
    }

    //==============================================================
    // Implementation
    //==============================================================

    @Override
    public Object evaluate(CatscriptRuntime runtime) {
        Integer lhsValueI = null, rhsValueI = null;
        String lhsValue = null, rhsValue = null;
        try {
            lhsValueI = (Integer) leftHandSide.evaluate(runtime);
            rhsValueI = (Integer) rightHandSide.evaluate(runtime);
        } catch (Exception e) {
            lhsValue = (String) leftHandSide.evaluate(runtime);
            rhsValue = (String) rightHandSide.evaluate(runtime);
        }

        if (lhsValue != null && rhsValue != null) {
            return (isAdd()) ? lhsValue + rhsValue : null;
        } else if (lhsValueI != null && rhsValueI != null) {
            return (isAdd()) ? lhsValueI + rhsValueI : lhsValueI - rhsValueI;
        }
        else if(lhsValue != null && rhsValueI != null){
            return new StringBuilder(lhsValue + rhsValueI);
        }
        else if(lhsValueI != null && rhsValue != null){
            return new StringBuilder(lhsValueI + rhsValue);
        }
        return null;
    }

    @Override
    public void transpile(StringBuilder javascript) {
        getLeftHandSide().transpile(javascript);
        javascript.append(operator.getStringValue());
        getRightHandSide().transpile(javascript);
    }

    @Override
    public void compile(ByteCodeGenerator code) {
        getLeftHandSide().compile(code);
        getRightHandSide().compile(code);
        if (isAdd()) {
            code.addInstruction(Opcodes.IADD);
        } else {
            code.addInstruction(Opcodes.ISUB);
        }
    }

}
