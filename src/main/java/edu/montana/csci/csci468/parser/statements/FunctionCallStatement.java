package edu.montana.csci.csci468.parser.statements;

import edu.montana.csci.csci468.bytecode.ByteCodeGenerator;
import edu.montana.csci.csci468.eval.CatscriptRuntime;
import edu.montana.csci.csci468.parser.CatscriptType;
import edu.montana.csci.csci468.parser.SymbolTable;
import edu.montana.csci.csci468.parser.expressions.Expression;
import edu.montana.csci.csci468.parser.expressions.FunctionCallExpression;
import org.objectweb.asm.Opcodes;

import java.util.LinkedList;
import java.util.List;

public class FunctionCallStatement extends Statement {
    private final FunctionCallExpression expression;

    public FunctionCallStatement(FunctionCallExpression parseExpression) {
        this.expression = addChild(parseExpression);
    }

    public List<Expression> getArguments() {
        return expression.getArguments();
    }

    @Override
    public void validate(SymbolTable symbolTable) {
        expression.validate(symbolTable);
    }

    public String getName() {
        return expression.getName();
    }

    //==============================================================
    // Implementation
    //==============================================================
    @Override
    public void execute(CatscriptRuntime runtime) {
        List<Object> args = new LinkedList<>();
        getArguments().forEach(expression1 -> args.add(expression1.evaluate(runtime)));
        getProgram().getFunction(getName()).invoke(runtime, args);
    }

    @Override
    public void transpile(StringBuilder javascript) {
        super.transpile(javascript);
    }

    @Override
    public void compile(ByteCodeGenerator code) {
        expression.compile(code);
        if(getProgram().getFunction(getName()).getType() != CatscriptType.VOID){
            code.addInstruction(Opcodes.POP);
        }
    }
}
