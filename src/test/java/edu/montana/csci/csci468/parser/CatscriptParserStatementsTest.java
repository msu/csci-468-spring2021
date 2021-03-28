package edu.montana.csci.csci468.parser;

import edu.montana.csci.csci468.CatscriptTestBase;
import edu.montana.csci.csci468.parser.expressions.*;
import edu.montana.csci.csci468.parser.statements.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CatscriptParserStatementsTest extends CatscriptTestBase {

    @Test
    public void printStatement() {
        PrintStatement expr = parseStatement("print(1)");
        assertNotNull(expr);
    }

    @Test
    public void printStatementEnsuresClosingParen() {
        PrintStatement expr = parseStatement("print(1", false);
        assertNotNull(expr);
        assertTrue(expr.hasErrors());
    }

    @Test
    public void forStatementParses() {
        ForStatement expr = parseStatement("for(x in [1, 2, 3]){ print(x) }");
        assertNotNull(expr);
        assertEquals("x", expr.getVariableName());
        assertTrue(expr.getExpression() instanceof ListLiteralExpression);
        assertEquals(1, expr.getBody().size());
    }

    @Test
    public void forStatementEnsuresClosingBrace() {
        ForStatement expr = parseStatement("for(x in [1, 2, 3]){ print(x)", false);
        assertNotNull(expr);
        assertTrue(expr.hasErrors());
    }

    @Test
    public void ifStatementParses() {
        IfStatement expr = parseStatement("if(x > 10){ print(x) }", false);
        assertNotNull(expr);
        assertTrue(expr.getExpression() instanceof ComparisonExpression);
        assertEquals(1, expr.getTrueStatements().size());
        assertEquals(0, expr.getElseStatements().size());
    }

    @Test
    public void ifStatementEnsuresClosingBrace() {
        IfStatement expr = parseStatement("if(x > 10){ print(x)", false);
        assertNotNull(expr);
        assertTrue(expr.hasErrors());
    }

    @Test
    public void ifStatementWithElseParses() {
        IfStatement expr = parseStatement("if(x > 10){ print(x) } else { print( 10 ) }", false);
        assertNotNull(expr);
        assertTrue(expr.getExpression() instanceof ComparisonExpression);
        assertEquals(1, expr.getTrueStatements().size());
        assertEquals(1, expr.getTrueStatements().size());
    }

    @Test
    public void elseStatementEnsuresClosingBrace() {
        IfStatement expr = parseStatement("if(x > 10){ print(x) } else { ", false);
        assertNotNull(expr);
        assertTrue(expr.hasErrors());
    }

    @Test
    public void varStatementWithImplicitType() {
        VariableStatement expr = parseStatement("var x = 10");
        assertNotNull(expr);
        assertEquals("x", expr.getVariableName());
        assertTrue(expr.getExpression() instanceof IntegerLiteralExpression);
    }

    @Test
    public void varStatementWithIntType() {
        VariableStatement expr = parseStatement("var x : int = 10");
        assertNotNull(expr);
        assertEquals("x", expr.getVariableName());
        assertEquals(CatscriptType.INT, expr.getExplicitType());
        assertTrue(expr.getExpression() instanceof IntegerLiteralExpression);
    }

    @Test
    public void varStatementWithBoolType() {
        VariableStatement expr = parseStatement("var x : bool = true");
        assertNotNull(expr);
        assertEquals("x", expr.getVariableName());
        assertEquals(CatscriptType.BOOLEAN, expr.getExplicitType());
        assertTrue(expr.getExpression() instanceof BooleanLiteralExpression);
    }

    @Test
    public void varStatementWithStringType() {
        VariableStatement expr = parseStatement("var x : string = \"\"");
        assertNotNull(expr);
        assertEquals("x", expr.getVariableName());
        assertEquals(CatscriptType.STRING, expr.getExplicitType());
        assertTrue(expr.getExpression() instanceof StringLiteralExpression);
    }

    @Test
    public void varStatementWithObjectType() {
        VariableStatement expr = parseStatement("var x : object = \"\"");
        assertNotNull(expr);
        assertEquals("x", expr.getVariableName());
        assertEquals(CatscriptType.OBJECT, expr.getExplicitType());
        assertTrue(expr.getExpression() instanceof StringLiteralExpression);
    }

    @Test
    public void varStatementWithListType() {
        VariableStatement expr = parseStatement("var x : list<int> = [1, 2, 3]");
        assertNotNull(expr);
        assertEquals("x", expr.getVariableName());
        assertEquals(CatscriptType.getListType(CatscriptType.INT), expr.getExplicitType());
        assertTrue(expr.getExpression() instanceof ListLiteralExpression);
    }

    @Test
    public void assigmentStatement() {
        AssignmentStatement expr = parseStatement("x = null", false);
        assertNotNull(expr);
        assertEquals("x", expr.getVariableName());
        assertTrue(expr.getExpression() instanceof NullLiteralExpression);
    }

    @Test
    public void functionCallStatement() {
        FunctionCallStatement expr = parseStatement("x(1, 2, 3)", false);
        assertNotNull(expr);
        assertEquals("x", expr.getName());
        assertEquals(3, expr.getArguments().size());
    }

    @Test
    public void functionDefStatement() {
        FunctionDefinitionStatement expr = parseStatement("function x() {}");
        assertNotNull(expr);
        assertEquals("x", expr.getName());
        assertEquals(0, expr.getParameterCount());
    }

    @Test
    public void functionDefWithParamsStatement() {
        FunctionDefinitionStatement expr = parseStatement("function x(a, b, c) {}");
        assertNotNull(expr);
        assertEquals("x", expr.getName());
        assertEquals(3, expr.getParameterCount());
        assertEquals("a", expr.getParameterName(0));
        assertEquals("b", expr.getParameterName(1));
        assertEquals("c", expr.getParameterName(2));
        assertEquals(CatscriptType.OBJECT, expr.getParameterType(0));
        assertEquals(CatscriptType.OBJECT, expr.getParameterType(1));
        assertEquals(CatscriptType.OBJECT, expr.getParameterType(2));
    }

    @Test
    public void functionDefWithParamTypesStatement() {
        FunctionDefinitionStatement expr = parseStatement("function x(a : object, b : int, c : bool) {}");
        assertNotNull(expr);
        assertEquals("x", expr.getName());
        assertEquals(3, expr.getParameterCount());
        assertEquals("a", expr.getParameterName(0));
        assertEquals("b", expr.getParameterName(1));
        assertEquals("c", expr.getParameterName(2));
        assertEquals(CatscriptType.OBJECT, expr.getParameterType(0));
        assertEquals(CatscriptType.INT, expr.getParameterType(1));
        assertEquals(CatscriptType.BOOLEAN, expr.getParameterType(2));
    }

    @Test
    public void returnStatementNoExprInFunction() {
        FunctionDefinitionStatement expr = parseStatement("function x() {return}");
        assertNotNull(expr);
        assertEquals("x", expr.getName());
        ReturnStatement returnStmt = (ReturnStatement) expr.getBody().get(0);
        assertNotNull(returnStmt);
    }

    @Test
    public void returnStatementExprInFunction() {
        FunctionDefinitionStatement expr = parseStatement("function x() : int {return 10}");
        assertNotNull(expr);
        assertEquals("x", expr.getName());
        ReturnStatement returnStmt = (ReturnStatement) expr.getBody().get(0);
        assertNotNull(returnStmt);
        assertTrue(returnStmt.getExpression() instanceof IntegerLiteralExpression);
    }

}
