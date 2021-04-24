package edu.montana.csci.csci468.parser.statements;

import edu.montana.csci.csci468.bytecode.ByteCodeGenerator;
import edu.montana.csci.csci468.eval.CatscriptRuntime;
import edu.montana.csci.csci468.eval.ContinueException;
import edu.montana.csci.csci468.parser.CatscriptType;
import edu.montana.csci.csci468.parser.ErrorType;
import edu.montana.csci.csci468.parser.SymbolTable;
import edu.montana.csci.csci468.parser.expressions.Expression;

import java.util.*;

public class SwitchStatement extends Statement {
    private Expression expression;
    private List<Statement> isStatements = Collections.emptyList();
    private CatscriptType type;

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = addChild(expression);
    }

    public void setIsStatements(List<Statement> statements) {
        this.isStatements = new LinkedList<>();
        for (Statement statement : statements) {
            this.isStatements.add(addChild(statement));
        }
    }


    @Override
    public void validate(SymbolTable symbolTable) {
        expression.validate(symbolTable);
        if (!expression.getType().equals(CatscriptType.INT) && !expression.getType().equals(CatscriptType.STRING)) {
            expression.addError(ErrorType.INCOMPATIBLE_TYPES);
        }
        symbolTable.pushScope();
        for (Statement isStatement : isStatements) {
            isStatement.validate(symbolTable);
        }
        symbolTable.popScope();
    }

    //==============================================================
    // Implementation
    //==============================================================
    @Override
    public void execute(CatscriptRuntime runtime) {
        runtime.pushScope();
        Object o = expression.evaluate(runtime);
        if (o != null) {
            boolean def = true;
            boolean execute = false;
            for (Statement isStatement : isStatements) {
                if (((IsStatement) isStatement).isDefault() && (def || execute)) {
                    isStatement.execute(runtime);
                    break;
                } else if (!((IsStatement) isStatement).isDefault()) {
                    Object x = ((IsStatement) isStatement).getExpression().evaluate(runtime);
                    if (x.equals(o) || execute) {
                        try {
                            def = false;
                            isStatement.execute(runtime);
                            break;
                        } catch (ContinueException c) {
                            if (isStatements.indexOf(isStatement) <= isStatements.size() - 2) {
                                execute = true;
                                continue;
                            }
                        }
                    }
                }
                execute = false;
            }
        }
        runtime.popScope();
    }

    @Override
    public void transpile(StringBuilder javascript) {
        super.transpile(javascript);
    }

    @Override
    public void compile(ByteCodeGenerator code) {

    }

    public CatscriptType getType() {
        return type;
    }

    public void setType(CatscriptType type) {
        this.type = type;
    }
}
