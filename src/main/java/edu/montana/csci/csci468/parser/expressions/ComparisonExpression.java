package edu.montana.csci.csci468.parser.expressions;

import edu.montana.csci.csci468.bytecode.ByteCodeGenerator;
import edu.montana.csci.csci468.eval.CatscriptRuntime;
import edu.montana.csci.csci468.parser.CatscriptType;
import edu.montana.csci.csci468.parser.ErrorType;
import edu.montana.csci.csci468.parser.ParseError;
import edu.montana.csci.csci468.parser.SymbolTable;
import edu.montana.csci.csci468.tokenizer.Token;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

import static edu.montana.csci.csci468.tokenizer.TokenType.*;

public class ComparisonExpression extends Expression {

    private final Token operator;
    private final Expression leftHandSide;
    private final Expression rightHandSide;

    public ComparisonExpression(Token operator, Expression leftHandSide, Expression rightHandSide) {
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

    @Override
    public String toString() {
        return super.toString() + "[" + operator.getStringValue() + "]";
    }

    public boolean isLessThan() {
        return operator.getType().equals(LESS);
    }

    public boolean isLessThanOrEqual() {
        return operator.getType().equals(LESS_EQUAL);
    }

    public boolean isGreaterThanOrEqual() {
        return operator.getType().equals(GREATER_EQUAL);
    }

    public boolean isGreater() {
        return operator.getType().equals(GREATER);
    }

    @Override
    public void validate(SymbolTable symbolTable) {
        leftHandSide.validate(symbolTable);
        rightHandSide.validate(symbolTable);
        if (!leftHandSide.getType().equals(CatscriptType.INT)) {
            leftHandSide.addError(ErrorType.INCOMPATIBLE_TYPES);
        }
        if (!rightHandSide.getType().equals(CatscriptType.INT)) {
            rightHandSide.addError(ErrorType.INCOMPATIBLE_TYPES);
        }
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
        Integer lhs = (Integer) leftHandSide.evaluate(runtime);
        Integer rhs = (Integer) rightHandSide.evaluate(runtime);
        if (operator.getStringValue().equals(">")) {
            return lhs > rhs;
        }
        if (operator.getStringValue().equals("<")) {
            return lhs < rhs;
        }
        if (operator.getStringValue().equals(">=")) {
            return lhs >= rhs;
        }
        if (operator.getStringValue().equals("<=")) {
            return lhs <= rhs;
        }
        return super.evaluate(runtime);
    }

    @Override
    public void transpile(StringBuilder javascript) {
        super.transpile(javascript);
    }

    @Override
    public void compile(ByteCodeGenerator code) {
        Label then = new Label();
        Label end = new Label();
        leftHandSide.compile(code);
        rightHandSide.compile(code);
        if (isGreater()) {
            code.addJumpInstruction(Opcodes.IF_ICMPLE, then);
        }
        if (isGreaterThanOrEqual()) {
            code.addJumpInstruction(Opcodes.IF_ICMPLT, then);
        }
        if (isLessThan()) {
            code.addJumpInstruction(Opcodes.IF_ICMPGE, then);
        }
        if(isLessThanOrEqual()){
            code.addJumpInstruction(Opcodes.IF_ICMPGT, then);
        }

        code.pushConstantOntoStack(1);
        code.addJumpInstruction(Opcodes.GOTO, end);
        code.addLabel(then);
        code.pushConstantOntoStack(0);
        code.addLabel(end);
    }

}
