package edu.montana.csci.csci468.parser.statements;

import edu.montana.csci.csci468.bytecode.ByteCodeGenerator;
import edu.montana.csci.csci468.eval.BreakExeption;
import edu.montana.csci.csci468.eval.CatscriptRuntime;
import edu.montana.csci.csci468.eval.ContinueException;
import edu.montana.csci.csci468.parser.ErrorType;
import edu.montana.csci.csci468.parser.SymbolTable;

public class ContinueStatement extends Statement {

    private Statement parentLoop;

    public void setParentLoop(Statement whileStatement) {
        this.parentLoop = whileStatement;
    }

    @Override
    public void validate(SymbolTable symbolTable) {
        if (parentLoop == null) {
            addError(ErrorType.UNKNOWN_NAME);
        }
    }

    //==============================================================
    // Implementation
    //==============================================================
    @Override
    public void execute(CatscriptRuntime runtime) {
        throw new ContinueException();
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