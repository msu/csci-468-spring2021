package edu.montana.csci.csci468.parser;

import edu.montana.csci.csci468.bytecode.ByteCodeGenerator;
import edu.montana.csci.csci468.eval.CatscriptRuntime;
import edu.montana.csci.csci468.parser.statements.CatScriptProgram;
import edu.montana.csci.csci468.parser.statements.FunctionDefinitionStatement;
import edu.montana.csci.csci468.tokenizer.Token;
import org.objectweb.asm.Opcodes;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static edu.montana.csci.csci468.bytecode.ByteCodeGenerator.internalNameFor;

public abstract class ParseElement {

    protected ParseElement parent;
    private Token start;
    private Token end;
    private List<ParseElement> children;
    private List<ParseError> errors;

    public ParseElement() {
        this.errors = new LinkedList<>();
        this.children = new LinkedList<>();
    }

    public CatScriptProgram getProgram() {
        if (this.getParent() instanceof CatScriptProgram) {
            return (CatScriptProgram) this.getParent();
        } else {
            return getParent().getProgram();
        }
    }

    public void setStart(Token start) {
        this.start = start;
    }

    public void setEnd(Token end) {
        this.end = end;
    }

    public void setToken(Token token) {
        setStart(token);
        setEnd(token);
    }

    public ParseElement getParent() {
        return parent;
    }

    public Token getStart() {
        return start;
    }

    public Token getEnd() {
        return end;
    }

    public List<ParseError> getErrors() {
        return errors;
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public void addError(ErrorType errorType, Object... args) {
        addError(errorType, getStart(), args);
    }

    public void addError(ErrorType errorMessage, Token token, Object... args) {
        errors.add(new ParseError(token, errorMessage, args));
    }

    protected <T extends ParseElement> T addChild(T element) {
        element.parent = this;
        children.add(element);
        return element;
    }

    public List<ParseElement> getChildren() {
        return children;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    public boolean hasError(ErrorType errorMessage) {
        return errors.stream().anyMatch(parseError -> Objects.equals(parseError.getErrorType(), errorMessage));
    }

    private void registerFunctions(SymbolTable symbolTable) {
        for (ParseElement child : children) {
            if (child instanceof FunctionDefinitionStatement) {
                FunctionDefinitionStatement func = (FunctionDefinitionStatement) child;
                if (symbolTable.hasSymbol(func.getName())) {
                    func.addError(ErrorType.DUPLICATE_NAME);
                } else {
                    symbolTable.registerFunction(func.getName(), func);
                }
            }
        }
    }


    public final void verify() {
        SymbolTable symbolTable = new SymbolTable();
        registerFunctions(symbolTable);
        validate(symbolTable);

        final LinkedList<ParseError> collector = new LinkedList<>();
        collectErrors(collector, this);
        if (collector.size() > 0) {
            throw new ParseErrorException(collector);
        }
    }

    public abstract void validate(SymbolTable symbolTable);

    private void collectErrors(LinkedList<ParseError> collector, ParseElement parseElement){
        collector.addAll(parseElement.getErrors());
        for (ParseElement child : parseElement.getChildren()) {
            collectErrors(collector, child);
        }
    }

    public void transpile(StringBuilder javascript) {
        throw new UnsupportedOperationException("transpile needs to be implemented for " + this.getClass().getName());
    }

    public void compile(ByteCodeGenerator code) {
        throw new UnsupportedOperationException("compile needs to be implemented for " + this.getClass().getName());
    }


    protected void box(ByteCodeGenerator code, CatscriptType type) {
        if (type.equals(CatscriptType.INT)) {
            code.addMethodInstruction(Opcodes.INVOKESTATIC, internalNameFor(Integer.class),
                    "valueOf", "(I)Ljava/lang/Integer;");
        }
        if (type.equals(CatscriptType.BOOLEAN)) {
            code.addMethodInstruction(Opcodes.INVOKESTATIC, internalNameFor(Boolean.class),
                    "valueOf", "(Z)Ljava/lang/Boolean;");
        }
    }

    protected void unbox(ByteCodeGenerator code, CatscriptType type) {
        if (type.equals(CatscriptType.INT)) {
            code.addMethodInstruction(Opcodes.INVOKEVIRTUAL, internalNameFor(Integer.class),
                    "intValue", "()I");
        }
        if (type.equals(CatscriptType.BOOLEAN)) {
            code.addMethodInstruction(Opcodes.INVOKEVIRTUAL, internalNameFor(Boolean.class),
                    "booleanValue", "()Z;");
        }
    }


}
