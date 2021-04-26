package edu.montana.csci.csci468.parser;

import edu.montana.csci.csci468.parser.expressions.*;
import edu.montana.csci.csci468.parser.statements.*;
import edu.montana.csci.csci468.tokenizer.CatScriptTokenizer;
import edu.montana.csci.csci468.tokenizer.Token;
import edu.montana.csci.csci468.tokenizer.TokenList;
import edu.montana.csci.csci468.tokenizer.TokenType;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static edu.montana.csci.csci468.tokenizer.TokenType.*;

public class CatScriptParser {

    private TokenList tokens;
    private FunctionDefinitionStatement currentFunctionDefinition;

    public CatScriptProgram parse(String source) {
        tokens = new CatScriptTokenizer(source).getTokens();

        // first parse an expression
        CatScriptProgram program = new CatScriptProgram();
        program.setStart(tokens.getCurrentToken());
        Expression expression = parseExpression();
        if (expression instanceof FunctionCallExpression) {
            program.addStatement(new FunctionCallStatement((FunctionCallExpression) expression));
        }
        if (tokens.hasMoreTokens()) {
            tokens.reset();
            while (tokens.hasMoreTokens()) {
                program.addStatement(parseProgramStatement());
            }
        } else {
            program.setExpression(expression);
        }

        program.setEnd(tokens.getCurrentToken());
        return program;
    }

    public CatScriptProgram parseAsExpression(String source) {
        tokens = new CatScriptTokenizer(source).getTokens();
        CatScriptProgram program = new CatScriptProgram();
        program.setStart(tokens.getCurrentToken());
        Expression expression = parseExpression();
        program.setExpression(expression);
        program.setEnd(tokens.getCurrentToken());
        return program;
    }

    //============================================================
    //  Statements
    //============================================================

    private Statement parseProgramStatement() {
        Statement printStmt = parsePrintStatement();
        if (printStmt != null) {
            return printStmt;
        }
        if (tokens.match(FOR)) {
            return parseForStatement();
        }
        if (tokens.match(IF)) {
            return parseIfStatement();
        }
        if (tokens.match(VAR)) {
            return parseVarStatement();
        }
        if (tokens.match(IDENTIFIER)) {
            Token variable_name = tokens.consumeToken();
            if (tokens.matchAndConsume(EQUAL)) {
                return parseAssignmentStatement(variable_name);
            } else if (tokens.match(LEFT_BRACKET)) {
                return parseArrayAssignmentStatement(variable_name);
            } else if (tokens.match(LEFT_PAREN)) {
                return new FunctionCallStatement(parseFunctionExpression(variable_name));
            }
        }
        if (tokens.match(FUNCTION)) {
            return parseFunctionDeclarationStatement();
        }
        if (tokens.match(RETURN)) {
            return parseReturnStatement();
        }
        if (tokens.match(WHILE)) {
            return parseWhileStatement();
        }
        if (tokens.match(BREAK)) {
            return parseBreakStatement();
        }
        if (tokens.match(CONTINUE)) {
            return parseContinueStatement();
        }
        if (tokens.match(DOWHILE)) {
            return parseDoWhileStatement();
        }
        if (tokens.match(SWITCH)) {
            return parseSwitchStatement();
        }
        if (tokens.match(IS, DEFAULT)) {
            return parseIsStatement();
        }
        return new SyntaxErrorStatement(tokens.consumeToken());
    }

    private Statement parseArrayAssignmentStatement(Token variable_name) {
        AssignmentStatement assignmentStatement = new AssignmentStatement();
        assignmentStatement.setStart(variable_name);
        assignmentStatement.setVariableName(variable_name.getStringValue());
        ArrayList<Expression> index = new ArrayList<>();
        while (tokens.matchAndConsume(LEFT_BRACKET) && tokens.hasMoreTokens()) {
            index.add(parseExpression());
            require(RIGHT_BRACKET, assignmentStatement);
        }
        assignmentStatement.setArrayIndex(index);
        require(EQUAL, assignmentStatement);
        Expression expression = parseExpression();
        assignmentStatement.setExpression(expression);
        assignmentStatement.setEnd(expression.getEnd());
        return assignmentStatement;
    }

    private Statement parseSwitchStatement() {
        SwitchStatement switchstatement = new SwitchStatement();
        switchstatement.setStart(tokens.consumeToken());
        require(LEFT_PAREN, switchstatement);
        Expression expression = parseExpression();
        switchstatement.setExpression(expression);
        switchstatement.setType(expression.getType());
        require(RIGHT_PAREN, switchstatement);
        require(LEFT_BRACE, switchstatement);
        List<Statement> body = new ArrayList<>();
        while (!tokens.match(RIGHT_BRACE) && tokens.hasMoreTokens()) {
            body.add(parseProgramStatement());
        }
        switchstatement.setIsStatements(body);
        require(RIGHT_BRACE, switchstatement);
        return switchstatement;
    }

    private Statement parseIsStatement() {
        IsStatement isStatement = new IsStatement();
        isStatement.setToken(tokens.consumeToken());

        if (!isStatement.isDefault()) {
            require(LEFT_PAREN, isStatement);
            isStatement.setExpression(parseExpression());
            require(RIGHT_PAREN, isStatement);
        }
        require(LEFT_BRACE, isStatement);
        List<Statement> body = new ArrayList<>();
        while (!tokens.match(RIGHT_BRACE) && tokens.hasMoreTokens()) {
            body.add(parseProgramStatement());
        }
        require(RIGHT_BRACE, isStatement);
        isStatement.setTrueStatements(body);
        return isStatement;
    }

    private Statement parseDoWhileStatement() {
        DoWhileStatement whileStatement = new DoWhileStatement();
        currentWhileStatement = whileStatement;
        whileStatement.setStart(tokens.consumeToken());
        require(LEFT_PAREN, whileStatement);
        whileStatement.setExpression(parseExpression());
        require(RIGHT_PAREN, whileStatement);
        require(LEFT_BRACE, whileStatement);
        List<Statement> body = new LinkedList<>();
        while (!tokens.match(RIGHT_BRACE) && tokens.hasMoreTokens()) {
            body.add(parseProgramStatement());
        }
        require(RIGHT_BRACE, whileStatement);
        currentWhileStatement = null;
        whileStatement.setBody(body);
        return whileStatement;
    }

    private Statement parseContinueStatement() {
        ContinueStatement continueStatement = new ContinueStatement();
        continueStatement.setStart(tokens.consumeToken());
        if (currentForStatement != null) {
            continueStatement.setParentLoop(currentForStatement);
        } else if (currentWhileStatement != null) {
            continueStatement.setParentLoop(currentWhileStatement);
        }
        return continueStatement;
    }

    private Statement parseBreakStatement() {
        BreakStatement breakStatement = new BreakStatement();
        breakStatement.setStart(tokens.consumeToken());
        if (currentForStatement != null) {
            breakStatement.setParentLoop(currentForStatement);
        } else if (currentWhileStatement != null) {
            breakStatement.setParentLoop(currentWhileStatement);
        }
        return breakStatement;
    }

    Statement currentWhileStatement = null;

    private Statement parseWhileStatement() {
        WhileStatement whileStatement = new WhileStatement();
        currentWhileStatement = whileStatement;
        whileStatement.setStart(tokens.consumeToken());
        require(LEFT_PAREN, whileStatement);
        whileStatement.setExpression(parseExpression());
        require(RIGHT_PAREN, whileStatement);
        require(LEFT_BRACE, whileStatement);
        List<Statement> body = new LinkedList<>();
        while (!tokens.match(RIGHT_BRACE) && tokens.hasMoreTokens()) {
            body.add(parseProgramStatement());
        }
        require(RIGHT_BRACE, whileStatement);
        currentWhileStatement = null;
        whileStatement.setBody(body);
        return whileStatement;
    }


    private Statement parseReturnStatement() {
        ReturnStatement returnStatement = new ReturnStatement();
        returnStatement.setStart(tokens.consumeToken());
        if (tokens.hasMoreTokens() && !tokens.match(RIGHT_BRACE)) {
            returnStatement.setExpression(parseExpression());
        }
        if (tempFunDef != null) {
            returnStatement.setFunctionDefinition(tempFunDef);
        }
        return returnStatement;
    }

    FunctionDefinitionStatement tempFunDef = null;

    private Statement parseFunctionDeclarationStatement() {
        FunctionDefinitionStatement functionDefinitionStatement = new FunctionDefinitionStatement();
        tempFunDef = functionDefinitionStatement;
        functionDefinitionStatement.setStart(tokens.consumeToken());
        functionDefinitionStatement.setName(require(IDENTIFIER, functionDefinitionStatement).getStringValue());
        require(LEFT_PAREN, functionDefinitionStatement);
        while (!tokens.match(RIGHT_PAREN) && tokens.hasMoreTokens()) {
            String name = require(IDENTIFIER, functionDefinitionStatement).getStringValue();
            TypeLiteral type = (tokens.matchAndConsume(COLON)) ? parseTypeLiteral() : null;
            functionDefinitionStatement.addParameter(name, type);
            tokens.matchAndConsume(COMMA);
        }
        require(RIGHT_PAREN, functionDefinitionStatement);
        if (tokens.matchAndConsume(COLON)) {
            functionDefinitionStatement.setType(parseTypeLiteral());
        } else {
            TypeLiteral type = new TypeLiteral();
            type.setType(CatscriptType.VOID);
            functionDefinitionStatement.setType(type);
        }
        require(LEFT_BRACE, functionDefinitionStatement);
        List<Statement> body = new LinkedList<>();
        while (tokens.hasMoreTokens() && !tokens.match(RIGHT_BRACE)) {
            body.add(parseProgramStatement());
        }
        require(RIGHT_BRACE, functionDefinitionStatement);
        functionDefinitionStatement.setBody(body);
        tempFunDef = null;
        return functionDefinitionStatement;
    }

    private Statement parseAssignmentStatement(Token name) {
        AssignmentStatement assignmentStatement = new AssignmentStatement();
        assignmentStatement.setStart(name);
        assignmentStatement.setVariableName(name.getStringValue());
        Expression expression = parseExpression();
        assignmentStatement.setExpression(expression);
        assignmentStatement.setEnd(expression.getEnd());
        return assignmentStatement;
    }

    private Statement parseVarStatement() {
        VariableStatement variableStatement = new VariableStatement();
        variableStatement.setStart(tokens.consumeToken());
        variableStatement.setVariableName(require(IDENTIFIER, variableStatement).getStringValue());
        if (tokens.matchAndConsume(COLON)) {
            variableStatement.setExplicitType(parseTypeLiteral().getType());
        }
        variableStatement.setEnd(require(EQUAL, variableStatement));
        variableStatement.setExpression(parseExpression());
        return variableStatement;
    }

    private Statement parseIfStatement() {
        IfStatement ifStatement = new IfStatement();
        ifStatement.setStart(tokens.consumeToken());
        require(LEFT_PAREN, ifStatement);
        ifStatement.setExpression(parseExpression());
        require(RIGHT_PAREN, ifStatement);
        require(LEFT_BRACE, ifStatement);
        LinkedList<Statement> body = new LinkedList<>();
        while (tokens.hasMoreTokens() && !tokens.match(RIGHT_BRACE)) {
            body.add(parseProgramStatement());
        }
        ifStatement.setTrueStatements(body);
        ifStatement.setEnd(require(RIGHT_BRACE, ifStatement));
        LinkedList<Statement> elseStatements = new LinkedList<>();
        if (tokens.matchAndConsume(ELSE)) {
            if (tokens.match(IF)) {
                elseStatements.add(parseIfStatement());
            } else {
                require(LEFT_BRACE, ifStatement);
                while (tokens.hasMoreTokens() && !tokens.match(RIGHT_BRACE)) {
                    elseStatements.add(parseProgramStatement());
                }
                require(RIGHT_BRACE, ifStatement);
            }
        }
        ifStatement.setElseStatements(elseStatements);
        return ifStatement;
    }

    ForStatement currentForStatement = new ForStatement();

    private Statement parseForStatement() {
        ForStatement forStatement = new ForStatement();
        currentForStatement = forStatement;
        forStatement.setStart(tokens.consumeToken());
        require(LEFT_PAREN, forStatement);
        if (tokens.match(IDENTIFIER)) {
            forStatement.setVariableName(tokens.getCurrentToken().getStringValue());
        }
        require(IDENTIFIER, forStatement);
        require(IN, forStatement);
        forStatement.setExpression(parseExpression());
        require(RIGHT_PAREN, forStatement);
        require(LEFT_BRACE, forStatement);
        List<Statement> statements = new LinkedList<>();
        while (tokens.hasMoreTokens() && !tokens.match(RIGHT_BRACE)) {
            statements.add(parseProgramStatement());
        }
        forStatement.setBody(statements);
        forStatement.setEnd(tokens.getCurrentToken());
        require(RIGHT_BRACE, forStatement);
        currentForStatement = null;
        return forStatement;
    }

    private Statement parsePrintStatement() {
        if (tokens.match(PRINT)) {

            PrintStatement printStatement = new PrintStatement();
            printStatement.setStart(tokens.consumeToken());

            require(LEFT_PAREN, printStatement);
            printStatement.setExpression(parseExpression());
            printStatement.setEnd(require(RIGHT_PAREN, printStatement));

            return printStatement;
        } else {
            return null;
        }
    }

    private TypeLiteral parseTypeLiteral() {
        TypeLiteral typeLiteral = new TypeLiteral();
        switch (tokens.getCurrentToken().getStringValue()) {
            case "int":
                tokens.consumeToken();
                typeLiteral.setType(CatscriptType.INT);
                break;
            case "string":
                tokens.consumeToken();
                typeLiteral.setType(CatscriptType.STRING);
                break;
            case "bool":
                tokens.consumeToken();
                typeLiteral.setType(CatscriptType.BOOLEAN);
                break;
            case "object":
                tokens.consumeToken();
                typeLiteral.setType(CatscriptType.OBJECT);
                break;
            case "list":
                tokens.consumeToken();
                if (tokens.matchAndConsume(LESS)) {
                    typeLiteral.setType(CatscriptType.getListType(parseTypeLiteral().getType()));
                    tokens.matchAndConsume(GREATER);
                } else {
                    typeLiteral.setType(CatscriptType.getListType(CatscriptType.OBJECT));
                }
                break;
        }
        return typeLiteral;
    }

    //============================================================
    //  Expressions
    //============================================================

    private Expression parseExpression() {
        if (tokens.match(RANGE)) {
            return parseRangeExpression();
        }
        return parseEqualityExpression();
    }

    private Expression parseRangeExpression() {
        RangeExpression rangeExpression = new RangeExpression();
        rangeExpression.setStart(tokens.consumeToken());
        require(LEFT_PAREN, rangeExpression);
        List<Expression> control = new ArrayList<>();
        while (!tokens.match(RIGHT_PAREN) && tokens.hasMoreTokens()) {
            control.add(parseExpression());
            tokens.matchAndConsume(COMMA);
        }
        rangeExpression.setControl(control);
        require(RIGHT_PAREN, rangeExpression);
        return rangeExpression;
    }

    private Expression parseAdditiveExpression() {
        Expression expression = parseFactorExpression();
        while (tokens.match(PLUS, MINUS)) {
            Token operator = tokens.consumeToken();
            Expression rightHandSide = (tokens.matchAndConsume(PLUS, MINUS)) ? new IntegerLiteralExpression("1") : parseFactorExpression();
            AdditiveExpression additiveExpression = new AdditiveExpression(operator, expression, rightHandSide);
            additiveExpression.setStart(expression.getStart());
            additiveExpression.setEnd(rightHandSide.getEnd());
            additiveExpression.setToken(operator);
            expression = additiveExpression;
        }
        return expression;
    }

    private Expression parseFactorExpression() {
        Expression expression = parseUnaryExpression();
        while (tokens.match(SLASH, STAR, MOD, OR, AND)) {
            Token operator = tokens.consumeToken();
            final Expression rightHandSide = parseUnaryExpression();
            FactorExpression factorExpression = new FactorExpression(operator, expression, rightHandSide);
            factorExpression.setStart(expression.getStart());
            factorExpression.setEnd(rightHandSide.getEnd());
            factorExpression.setToken(operator);
            expression = factorExpression;
        }
        return expression;

    }

    private Expression parseEqualityExpression() {
        Expression lhs = parseComparisonExpression();
        if (tokens.match(EQUAL_EQUAL, BANG_EQUAL)) {
            Token token = tokens.consumeToken();
            Expression rhs = parseEqualityExpression();
            rhs.setToken(token);
            return new EqualityExpression(token, lhs, rhs);
        } else {
            return lhs;
        }
    }

    private Expression parseComparisonExpression() {
        Expression lhs = parseAdditiveExpression();
        if (tokens.match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
            Token token = tokens.consumeToken();
            Expression rhs = parseAdditiveExpression();
            rhs.setToken(token);
            return new ComparisonExpression(token, lhs, rhs);
        } else {
            return lhs;
        }
    }

    private Expression parseUnaryExpression() {
        if (tokens.match(MINUS, NOT)) {
            Token token = tokens.consumeToken();
            Expression rhs = parseUnaryExpression();
            UnaryExpression unaryExpression = new UnaryExpression(token, rhs);
            unaryExpression.setStart(token);
            unaryExpression.setEnd(rhs.getEnd());
            return unaryExpression;
        } else {
            return parsePrimaryExpression();
        }
    }

    private FunctionCallExpression parseFunctionExpression(Token start) {
        List<Expression> argumentList = new ArrayList<>();
        boolean terminated = true;
        tokens.consumeToken();
        if (!tokens.matchAndConsume(RIGHT_PAREN)) {
            argumentList.add(parseExpression());
            while (tokens.matchAndConsume(COMMA)) {
                argumentList.add(parseExpression());
            }
            terminated = false;
        }
        FunctionCallExpression functionCallExpression = new FunctionCallExpression(start.getStringValue(), argumentList);
        functionCallExpression.setToken(start);
        if (!terminated) {
            require(RIGHT_PAREN, functionCallExpression, ErrorType.UNTERMINATED_ARG_LIST);
        }
        return functionCallExpression;
    }

    // With enough if statements i can do anything
    private Expression parsePrimaryExpression() {
        Token token;
        if (tokens.match(INTEGER)) {
            token = tokens.consumeToken();
            IntegerLiteralExpression integerExpression = new IntegerLiteralExpression(token.getStringValue());
            integerExpression.setToken(token);
            return integerExpression;
        } else if (tokens.match(IDENTIFIER)) {
            token = tokens.consumeToken();
            if (tokens.match(LEFT_PAREN)) {
                return parseFunctionExpression(token);
            }
            if (tokens.match(LEFT_BRACKET)) {
                return parseIndexExpression(token);
            }
            if (tokens.match(QUESTION)) {
                return parseNullCheckExpression(token);
            } else {
                IdentifierExpression identifierExpression = new IdentifierExpression(token.getStringValue());
                identifierExpression.setToken(token);
                return identifierExpression;
            }
        } else if (tokens.match(STRING)) {
            token = tokens.consumeToken();
            StringLiteralExpression stringExpression = new StringLiteralExpression(token.getStringValue());
            stringExpression.setToken(token);
            return stringExpression;
        } else if (tokens.match(TRUE)) {
            token = tokens.consumeToken();
            BooleanLiteralExpression booleanExpression = new BooleanLiteralExpression(true);
            booleanExpression.setToken(token);
            return booleanExpression;
        } else if (tokens.match(FALSE)) {
            token = tokens.consumeToken();
            BooleanLiteralExpression booleanExpression = new BooleanLiteralExpression(false);
            booleanExpression.setToken(token);
            return booleanExpression;
        } else if (tokens.match(NULL)) {
            token = tokens.consumeToken();
            NullLiteralExpression nullLiteralExpression = new NullLiteralExpression();
            nullLiteralExpression.setToken(token);
            return nullLiteralExpression;
        } else if (tokens.matchAndConsume(LEFT_BRACKET)) {
            List<Expression> listArguments = new ArrayList<>(0);
            boolean terminated = true;
            if (!tokens.matchAndConsume(RIGHT_BRACKET)) {
                listArguments.add(parseExpression());
                while (tokens.matchAndConsume(COMMA)) {
                    listArguments.add(parseExpression());
                }
                terminated = false;
            }
            ListLiteralExpression listLiteralExpression = new ListLiteralExpression(listArguments);
            if (!terminated) {
                require(RIGHT_BRACKET, listLiteralExpression, ErrorType.UNTERMINATED_LIST);
            }
            return listLiteralExpression;
        } else if (tokens.matchAndConsume(LEFT_PAREN)) {
            Expression expression = parseExpression();
            ParenthesizedExpression parenthesizedExpression = new ParenthesizedExpression(expression);
            require(RIGHT_PAREN, parenthesizedExpression);
            if(tokens.matchAndConsume(QUESTION)){
                return parseTernaryExpression(parenthesizedExpression);
            }
            return parenthesizedExpression;
        }
        return new SyntaxErrorExpression(tokens.consumeToken());
    }

    private Expression parseTernaryExpression(ParenthesizedExpression parenthesizedExpression) {
        TernaryExpression ternaryExpression = new TernaryExpression();
        ternaryExpression.setParenthesizedExpression(parenthesizedExpression);
        ternaryExpression.setTrueExpression(parseExpression());
        require(COLON, ternaryExpression);
        ternaryExpression.setFalseExpression(parseExpression());
        return ternaryExpression;
    }


    private Expression parseNullCheckExpression(Token variableName) {
        NullCheckExpression nullCheckExpression = new NullCheckExpression();
        nullCheckExpression.setVariable(variableName);
        nullCheckExpression.setStart(variableName);
        nullCheckExpression.setEnd(tokens.consumeToken());
        return nullCheckExpression;
    }

    private Expression parseIndexExpression(Token variableName) {
        IndexExpression indexExpression = new IndexExpression();
        indexExpression.setStart(tokens.getCurrentToken());
        ArrayList<Expression> indexes = new ArrayList<>();
        while(tokens.matchAndConsume(LEFT_BRACKET) && tokens.hasMoreTokens()){
            indexes.add(parsePrimaryExpression());
            require(RIGHT_BRACKET, indexExpression);
        }
        indexExpression.setVariableName(variableName.getStringValue());
        indexExpression.setIndexes(indexes);
        return indexExpression;
    }


    //============================================================
    //  Parse Helpers
    //============================================================
    private Token require(TokenType type, ParseElement elt) {
        return require(type, elt, ErrorType.UNEXPECTED_TOKEN);
    }

    private Token require(TokenType type, ParseElement elt, ErrorType msg) {
        if (tokens.match(type)) {
            return tokens.consumeToken();
        } else {
            elt.addError(msg, tokens.getCurrentToken());
            return tokens.getCurrentToken();
        }
    }

}
