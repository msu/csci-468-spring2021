package edu.montana.csci.csci468.parser.expressions;

import edu.montana.csci.csci468.bytecode.ByteCodeGenerator;
import edu.montana.csci.csci468.eval.CatscriptRuntime;
import edu.montana.csci.csci468.parser.CatscriptType;
import edu.montana.csci.csci468.parser.SymbolTable;
import org.objectweb.asm.Opcodes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static edu.montana.csci.csci468.bytecode.ByteCodeGenerator.internalNameFor;

public class ListLiteralExpression extends Expression {
    List<Expression> values;
    private CatscriptType type;

    public ListLiteralExpression(List<Expression> values) {
        this.values = new LinkedList<>();
        for (Expression value : values) {
            this.values.add(addChild(value));
        }
    }

    public List<Expression> getValues() {
        return values;
    }

    @Override
    public void validate(SymbolTable symbolTable) {
        for (Expression value : values) {
            value.validate(symbolTable);
        }
        if (values.size() > 0) {
            CatscriptType runningType = values.get(0).getType();
            for (Expression value : values) {
                if (!runningType.isAssignableFrom(value.getType())) {
                    if(runningType == CatscriptType.NULL){
                        runningType = value.getType();
                    }
                    else{
                        runningType = CatscriptType.OBJECT;
                    }
                }
            }
            type = CatscriptType.getListType(runningType);
        } else {
            type = CatscriptType.getListType(CatscriptType.OBJECT);
        }
    }

    @Override
    public CatscriptType getType() {
        return type;
    }

    //==============================================================
    // Implementation
    //==============================================================

    @Override
    public Object evaluate(CatscriptRuntime runtime) {
        ArrayList<Object> arrayList = new ArrayList<>();
        values.forEach(expression -> arrayList.add(expression.evaluate(runtime)));
        return arrayList;
    }

    @Override
    public void transpile(StringBuilder javascript) {
        super.transpile(javascript);
    }

    @Override
    public void compile(ByteCodeGenerator code) {
        code.addTypeInstruction(Opcodes.NEW, internalNameFor(LinkedList.class));
        code.addInstruction(Opcodes.DUP);
        code.addMethodInstruction(Opcodes.INVOKESPECIAL,internalNameFor(LinkedList.class),"<init>","()V");
        values.forEach(value -> {
            code.addInstruction(Opcodes.DUP);
            value.compile(code);
            box(code, value.getType());
            code.addMethodInstruction(Opcodes.INVOKEVIRTUAL, internalNameFor(LinkedList.class),"add","(Ljava/lang/Object;)Z");
            code.addInstruction(Opcodes.POP);
        });
    }


}
