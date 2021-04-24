package edu.montana.csci.csci468.parser.expressions;

import edu.montana.csci.csci468.bytecode.ByteCodeGenerator;
import edu.montana.csci.csci468.eval.CatscriptRuntime;
import edu.montana.csci.csci468.parser.CatscriptType;
import edu.montana.csci.csci468.parser.SymbolTable;
import edu.montana.csci.csci468.tokenizer.Token;
import edu.montana.csci.csci468.tokenizer.TokenType;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

public class EqualityExpression extends Expression {

    private final Token operator;
    private final Expression leftHandSide;
    private final Expression rightHandSide;

    public EqualityExpression(Token operator, Expression leftHandSide, Expression rightHandSide) {
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

    public boolean isEqual() {
        return operator.getType().equals(TokenType.EQUAL_EQUAL);
    }

    @Override
    public void validate(SymbolTable symbolTable) {
        leftHandSide.validate(symbolTable);
        rightHandSide.validate(symbolTable);
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
        return isEqual() == (getRightHandSide().evaluate(runtime) == getLeftHandSide().evaluate(runtime));
    }

    @Override
    public void transpile(StringBuilder javascript) {
        super.transpile(javascript);
    }

    @Override
    public void compile(ByteCodeGenerator code) {
        Label then = new Label();
        Label end = new Label();
        if (isEqual()) {
            leftHandSide.compile(code);
            box(code, leftHandSide.getType());
            rightHandSide.compile(code);
            box(code, rightHandSide.getType());
            code.addJumpInstruction(Opcodes.IF_ACMPNE, then);
        } else {
            leftHandSide.compile(code);
            box(code, leftHandSide.getType());
            rightHandSide.compile(code);
            box(code, rightHandSide.getType());
            code.addJumpInstruction(Opcodes.IF_ACMPEQ, then);
        }
        code.pushConstantOntoStack(1);
        code.addJumpInstruction(Opcodes.GOTO, end);
        code.addLabel(then);
        code.pushConstantOntoStack(0);
        code.addLabel(end);
    }


}
