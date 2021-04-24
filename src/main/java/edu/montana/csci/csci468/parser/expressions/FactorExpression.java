package edu.montana.csci.csci468.parser.expressions;

import edu.montana.csci.csci468.bytecode.ByteCodeGenerator;
import edu.montana.csci.csci468.eval.CatscriptRuntime;
import edu.montana.csci.csci468.parser.CatscriptType;
import edu.montana.csci.csci468.parser.ErrorType;
import edu.montana.csci.csci468.parser.SymbolTable;
import edu.montana.csci.csci468.tokenizer.Token;
import edu.montana.csci.csci468.tokenizer.TokenType;
import org.objectweb.asm.Opcodes;

public class FactorExpression extends Expression {

    private final Token operator;
    private final Expression leftHandSide;
    private final Expression rightHandSide;

    public FactorExpression(Token operator, Expression leftHandSide, Expression rightHandSide) {
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

    public boolean isMultiply() {
        return operator.getType() == TokenType.STAR;
    }

    public boolean isMod() {
        return operator.getType() == TokenType.MOD;
    }

    public boolean isBool() {
        return operator.getType() == TokenType.AND || operator.getType() == TokenType.OR;
    }

    public boolean isAnd() {
        return operator.getType() == TokenType.AND;
    }

    @Override
    public String toString() {
        return super.toString() + "[" + operator.getStringValue() + "]";
    }

    @Override
    public void validate(SymbolTable symbolTable) {
        leftHandSide.validate(symbolTable);
        rightHandSide.validate(symbolTable);
        if (isBool()) {
            if (!leftHandSide.getType().equals(CatscriptType.BOOLEAN) && !rightHandSide.getType().equals(CatscriptType.BOOLEAN)) {
                addError(ErrorType.INCOMPATIBLE_TYPES);
            }
        } else {
            if (!leftHandSide.getType().equals(CatscriptType.INT)) {
                leftHandSide.addError(ErrorType.INCOMPATIBLE_TYPES);
            }
            if (!rightHandSide.getType().equals(CatscriptType.INT)) {
                rightHandSide.addError(ErrorType.INCOMPATIBLE_TYPES);
            }
        }
    }

    @Override
    public CatscriptType getType() {
        if (isBool()) {
            return CatscriptType.BOOLEAN;
        }
        return CatscriptType.INT;
    }

    //==============================================================
    // Implementation
    //==============================================================

    @Override
    public Object evaluate(CatscriptRuntime runtime) {
        if (this.isBool()) {
            return (isAnd()) ? (boolean) leftHandSide.evaluate(runtime) && (boolean) rightHandSide.evaluate(runtime) :
                    (boolean) leftHandSide.evaluate(runtime) || (boolean) rightHandSide.evaluate(runtime);
        } else {
            Integer rhs = (Integer) rightHandSide.evaluate(runtime);
            Integer lhs = (Integer) leftHandSide.evaluate(runtime);
            if (this.isMultiply()) {
                return lhs * rhs;
            }
            if (this.isMod()) {
                return lhs % rhs;
            } else {
                return lhs / rhs;
            }
        }
    }

    @Override
    public void transpile(StringBuilder javascript) {
        super.transpile(javascript);
    }

    @Override
    public void compile(ByteCodeGenerator code) {
        getLeftHandSide().compile(code);
        getRightHandSide().compile(code);
        if (isMultiply()) {
            code.addInstruction(Opcodes.IMUL);
        } else {
            code.addInstruction(Opcodes.IDIV);
        }
    }


}
