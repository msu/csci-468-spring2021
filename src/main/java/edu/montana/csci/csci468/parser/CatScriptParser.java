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
            }
        }
        if (tokens.match(FUNCTION)) {
            return parseFunctionDeclarationStatement();
        }
        return new SyntaxErrorStatement(tokens.consumeToken());
    }

    private Statement parseReturnStatement() {
        ReturnStatement returnStatement = new ReturnStatement();
        returnStatement.setStart(tokens.consumeToken());
        if (tokens.hasMoreTokens() && !tokens.match(RIGHT_BRACE)) {
            returnStatement.setExpression(parseExpression());
        }
        return returnStatement;
    }

    private Statement parseFunctionDeclarationStatement() {
        FunctionDefinitionStatement functionDefinitionStatement = new FunctionDefinitionStatement();
        functionDefinitionStatement.setStart(tokens.consumeToken());
        functionDefinitionStatement.setName(require(IDENTIFIER, functionDefinitionStatement).getStringValue());
        require(LEFT_PAREN, functionDefinitionStatement);
        while (!tokens.match(RIGHT_PAREN) && tokens.hasMoreTokens()) {
            String name = require(IDENTIFIER, functionDefinitionStatement).getStringValue();
            TypeLiteral type = null;
            if (tokens.matchAndConsume(COLON)) {
                type = parseTypeLiteral();
            }
            functionDefinitionStatement.addParameter(name, type);
            tokens.matchAndConsume(COMMA);
        }
        require(RIGHT_PAREN, functionDefinitionStatement);
        if (tokens.match(COLON)) {
            tokens.consumeToken();
            functionDefinitionStatement.setType(parseTypeLiteral());
        } else {
            TypeLiteral type = new TypeLiteral();
            type.setType(CatscriptType.VOID);
            functionDefinitionStatement.setType(type);
        }
        require(LEFT_BRACE, functionDefinitionStatement);
        List<Statement> body = new LinkedList<>();
        while (tokens.hasMoreTokens() && !tokens.match(RIGHT_BRACE)) {
            Statement statement;
            if(tokens.match(RETURN)){
                statement = parseReturnStatement();
                ((ReturnStatement) statement).setFunctionDefinition(functionDefinitionStatement);
            }
            else{
                statement = parseProgramStatement();
            }
            body.add(statement);
        }
        require(RIGHT_BRACE, functionDefinitionStatement);
        functionDefinitionStatement.setBody(body);
        return functionDefinitionStatement;
    }

    private Statement parseAssignmentStatement(Token start) {
        AssignmentStatement assignmentStatement = new AssignmentStatement();
        assignmentStatement.setStart(start);
        assignmentStatement.setVariableName(start.getStringValue());
        Expression expression = parseExpression();
        assignmentStatement.setExpression(expression);
        assignmentStatement.setEnd(expression.getEnd());
        return assignmentStatement;
    }

    private Statement parseVarStatement() {
        VariableStatement variableStatement = new VariableStatement();
        variableStatement.setStart(tokens.consumeToken());
        variableStatement.setVariableName(require(IDENTIFIER, variableStatement).getStringValue());
        CatscriptType explicitType = null;
        if (tokens.matchAndConsume(COLON)) {
            explicitType = parseTypeLiteral().getType();
        }
        variableStatement.setEnd(require(EQUAL, variableStatement));
        variableStatement.setExpression(parseExpression());
        variableStatement.setExplicitType(explicitType);
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
        if (elseStatements.size() > 0) {
            ifStatement.setElseStatements(elseStatements);
        }
        return ifStatement;
    }

    private Statement parseForStatement() {
        ForStatement forStatement = new ForStatement();
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
        if (tokens.getCurrentToken().getStringValue().equals("int")) {
            tokens.consumeToken();
            typeLiteral.setType(CatscriptType.INT);
        } else if (tokens.getCurrentToken().getStringValue().equals("string")) {
            tokens.consumeToken();
            typeLiteral.setType(CatscriptType.STRING);
        } else if (tokens.getCurrentToken().getStringValue().equals("bool")) {
            tokens.consumeToken();
            typeLiteral.setType(CatscriptType.BOOLEAN);
        } else if (tokens.getCurrentToken().getStringValue().equals("object")) {
            tokens.consumeToken();
            typeLiteral.setType(CatscriptType.OBJECT);
        } else if (tokens.getCurrentToken().getStringValue().equals("list")) {
            tokens.consumeToken();
            require(LESS, typeLiteral);
            typeLiteral.setType(CatscriptType.getListType(parseTypeLiteral().getType()));
            require(GREATER, typeLiteral);
        }
        return typeLiteral;
    }

    //============================================================
    //  Expressions
    //============================================================

    private Expression parseExpression() {
        return parseEqualityExpression();
    }

    private Expression parseAdditiveExpression() {
        Expression expression = parseFactorExpression();
        while (tokens.match(PLUS, MINUS)) {
            Token operator = tokens.consumeToken();
            final Expression rightHandSide = parseFactorExpression();
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
        while (tokens.match(SLASH, STAR)) {
            Token operator = tokens.consumeToken();
            final Expression rightHandSide = parseUnaryExpression();
            FactorExpression additiveExpression = new FactorExpression(operator, expression, rightHandSide);
            additiveExpression.setStart(expression.getStart());
            additiveExpression.setEnd(rightHandSide.getEnd());
            additiveExpression.setToken(operator);
            expression = additiveExpression;
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
            if (tokens.matchAndConsume(LEFT_PAREN)) {
                return parseFunctionExpression(token);
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
            ListLiteralExpression functionCallExpression = new ListLiteralExpression(listArguments);
            if (!terminated) {
                require(RIGHT_BRACKET, functionCallExpression, ErrorType.UNTERMINATED_LIST);
            }
            return functionCallExpression;
        } else if (tokens.matchAndConsume(LEFT_PAREN)) {
            Expression expression = parseExpression();
            return new ParenthesizedExpression(expression);
        }
        return new SyntaxErrorExpression(tokens.consumeToken());
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
