package edu.montana.csci.csci468.parser.statements;

import edu.montana.csci.csci468.bytecode.ByteCodeGenerator;
import edu.montana.csci.csci468.eval.BreakExeption;
import edu.montana.csci.csci468.eval.CatscriptRuntime;
import edu.montana.csci.csci468.eval.ReturnException;
import edu.montana.csci.csci468.parser.CatscriptType;
import edu.montana.csci.csci468.parser.ErrorType;
import edu.montana.csci.csci468.parser.SymbolTable;
import edu.montana.csci.csci468.parser.expressions.Expression;
import org.objectweb.asm.Opcodes;

public class BreakStatement extends Statement {

    private WhileStatement whileStatement;

    public void setWhileStatement(WhileStatement whileStatement) {
        this.whileStatement = whileStatement;
    }

    @Override
    public void validate(SymbolTable symbolTable) {
        if (whileStatement == null) {
            addError(ErrorType.UNKNOWN_NAME);
        }
    }

    //==============================================================
    // Implementation
    //==============================================================
    @Override
    public void execute(CatscriptRuntime runtime) {
        throw new BreakExeption();
    }

    @Override
    public void transpile(StringBuilder javascript) {
        super.transpile(javascript);
    }

    @Override
    public void compile(ByteCodeGenerator code) {
        super.compile(code);
    }

}