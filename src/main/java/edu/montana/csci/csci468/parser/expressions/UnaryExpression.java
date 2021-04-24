package edu.montana.csci.csci468.parser.expressions;

import edu.montana.csci.csci468.bytecode.ByteCodeGenerator;
import edu.montana.csci.csci468.eval.CatscriptRuntime;
import edu.montana.csci.csci468.parser.CatscriptType;
import edu.montana.csci.csci468.parser.ErrorType;
import edu.montana.csci.csci468.parser.ParseError;
import edu.montana.csci.csci468.parser.SymbolTable;
import edu.montana.csci.csci468.tokenizer.Token;
import edu.montana.csci.csci468.tokenizer.TokenType;
import org.objectweb.asm.Opcodes;

public class UnaryExpression extends Expression {

    private final Token operator;
    private final Expression rightHandSide;

    public UnaryExpression(Token operator, Expression rightHandSide) {
        this.rightHandSide = addChild(rightHandSide);
        this.operator = operator;
    }

    public Expression getRightHandSide() {
        return rightHandSide;
    }

    public boolean isMinus() {
        return operator.getType().equals(TokenType.MINUS);
    }

    public boolean isNot() {
        return !isMinus();
    }

    @Override
    public String toString() {
        return super.toString() + "[" + operator.getStringValue() + "]";
    }

    @Override
    public void validate(SymbolTable symbolTable) {
        rightHandSide.validate(symbolTable);
        if (isNot() && !rightHandSide.getType().equals(CatscriptType.BOOLEAN)) {
            addError(ErrorType.INCOMPATIBLE_TYPES);
        } else if (isMinus() && !rightHandSide.getType().equals(CatscriptType.INT)) {
            addError(ErrorType.INCOMPATIBLE_TYPES);
        }
    }

    @Override
    public CatscriptType getType() {
        return (isMinus()) ? CatscriptType.INT : CatscriptType.BOOLEAN;
    }

    //==============================================================
    // Implementation
    //==============================================================

    @Override
    public Object evaluate(CatscriptRuntime runtime) {
        Object rhsValue = getRightHandSide().evaluate(runtime);
        return (isMinus()) ? -1 * (Integer) rhsValue : !(Boolean) rhsValue;
    }

    @Override
    public void transpile(StringBuilder javascript) {
        super.transpile(javascript);
    }

    @Override
    public void compile(ByteCodeGenerator code) {
        rightHandSide.compile(code);
        if (isNot()) {
            code.addInstruction(Opcodes.ICONST_1);
            code.addInstruction(Opcodes.IADD);
            code.addInstruction(Opcodes.ICONST_2);
            code.addInstruction(Opcodes.IREM);
        }
        if (isMinus()) {
            code.pushConstantOntoStack(-1);
            code.addInstruction(Opcodes.IMUL);
        }
    }


}
