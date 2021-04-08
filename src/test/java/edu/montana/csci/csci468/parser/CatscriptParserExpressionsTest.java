package edu.montana.csci.csci468.parser;

import edu.montana.csci.csci468.CatscriptTestBase;
import edu.montana.csci.csci468.parser.expressions.*;
import edu.montana.csci.csci468.parser.statements.CatScriptProgram;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class  CatscriptParserExpressionsTest extends CatscriptTestBase {

    @Test
    public void parseIntegerLiteralWorks() {
        IntegerLiteralExpression ile = parseExpression("1");
        assertEquals(1, ile.getValue());
    }

    @Test
    public void parseStringLiteralWorks() {
        StringLiteralExpression expr = parseExpression("\"asdf\"");
        assertEquals("asdf", expr.getValue());
    }

    @Test
    public void parseAddExpressionWorks() {
        AdditiveExpression expr = parseExpression("1 + 1");
        assertTrue(expr.isAdd());
    }

    @Test
    public void additiveExpressionsAreLeftAssociative() {
        AdditiveExpression expr = parseExpression("1 + 1 + 1");
        assertTrue(expr.isAdd());
        assertTrue(expr.getLeftHandSide() instanceof AdditiveExpression);
        assertTrue(expr.getRightHandSide() instanceof IntegerLiteralExpression);
    }

    @Test
    public void additiveExpressionsCanBeParenthesized() {
        AdditiveExpression expr = parseExpression("1 + (1 + 1)");
        assertTrue(expr.isAdd());
        assertTrue(expr.getLeftHandSide() instanceof IntegerLiteralExpression);
        assertTrue(expr.getRightHandSide() instanceof ParenthesizedExpression);
    }

    @Test
    public void parseSubtractionExpressionWorks() {
        AdditiveExpression expr = parseExpression("1 - 1");
        assertFalse(expr.isAdd());
    }

    @Test
    public void parseTrueExpression() {
        BooleanLiteralExpression expr = parseExpression("true");
        assertTrue(expr.getValue());
    }

    @Test
    public void parseFalseExpression() {
        BooleanLiteralExpression expr = parseExpression("false");
        assertFalse(expr.getValue());
    }

    @Test
    public void parseNullExpression() {
        NullLiteralExpression expr = parseExpression("null");
        assertNotNull(expr);
    }

    @Test
    public void parseIdentifierExpression() {
        IdentifierExpression expr = parseExpression("x", false);
        assertEquals("x", expr.getName());
    }

    @Test
    public void parseListLiteralExpression() {
        ListLiteralExpression expr = parseExpression("[1, 2, 3]");
        assertEquals(3, expr.getValues().size());
    }

    @Test
    public void parseEmptyListLiteralExpression() {
        ListLiteralExpression expr = parseExpression("[]");
        assertEquals(0, expr.getValues().size());
    }

    @Test
    public void parseUnterminatedListLiteralExpression() {
        ListLiteralExpression expr = parseExpression("[1, 2", false);
        assertEquals(2, expr.getValues().size());
        assertTrue(expr.hasError(ErrorType.UNTERMINATED_LIST));
    }

    @Test
    public void parseFunctionCallExpression() {
        FunctionCallExpression expr = parseExpression("foo(1, 2, 3)", false);
        assertEquals("foo", expr.getName());
        assertEquals(3, expr.getArguments().size());
    }

    @Test
    public void parseNoArgFunctionCallExpression() {
        FunctionCallExpression expr = parseExpression("foo()", false);
        assertEquals("foo", expr.getName());
        assertEquals(0, expr.getArguments().size());
    }

    @Test
    public void parseUnterminatedFunctionCallExpression() {
        FunctionCallExpression expr = parseExpression("foo(1, 2", false);
        assertEquals("foo", expr.getName());
        assertEquals(2, expr.getArguments().size());
        assertTrue(expr.hasError(ErrorType.UNTERMINATED_ARG_LIST));
    }

    @Test
    public void parseNegativeUnaryExpression() {
        UnaryExpression expr = parseExpression("-1");
        assertEquals(true, expr.isMinus());
        assertTrue(expr.getRightHandSide() instanceof IntegerLiteralExpression);
    }

    @Test
    public void tim(){
        AdditiveExpression expression = parseExpression("1 + \"a\"");
        assertEquals(CatscriptType.INT,expression.getLeftHandSide().getType());
        assertEquals(CatscriptType.STRING, expression.getRightHandSide().getType());
    }

    @Test
    public void parseNotUnaryExpression() {
        UnaryExpression expr = parseExpression("not true");
        assertEquals(true, expr.isNot());
        assertTrue(expr.getRightHandSide() instanceof BooleanLiteralExpression);
    }

    @Test
    public void parseNestedUnaryExpression() {
        UnaryExpression expr = parseExpression("not not true");
        assertEquals(true, expr.isNot());
        assertTrue(expr.getRightHandSide() instanceof UnaryExpression);
    }

    @Test
    public void parseMultiplyExpressionWorks() {
        FactorExpression expr = parseExpression("1 * 1");
        assertTrue(expr.isMultiply());
    }

    @Test
    public void parseDivideExpressionWorks() {
        FactorExpression expr = parseExpression("1 / 1");
        assertFalse(expr.isMultiply());
    }

    @Test
    public void parseFactorIsHigherPrecendenceThanAdd() {
        AdditiveExpression  expr = parseExpression("1 * 1 + 1 / 1");
        assertTrue(expr.isAdd());
        assertTrue(expr.getLeftHandSide() instanceof FactorExpression);
        assertTrue(expr.getRightHandSide() instanceof FactorExpression);
    }

    @Test
    public void factorExpressionsAreLeftAssociative() {
        FactorExpression expr = parseExpression("1 * 1 * 1");
        assertTrue(expr.isMultiply());
        assertTrue(expr.getLeftHandSide() instanceof FactorExpression);
        assertTrue(expr.getRightHandSide() instanceof IntegerLiteralExpression);
    }

    @Test
    public void lessThanExpression() {
        ComparisonExpression expr = parseExpression("1 < 1");
        assertTrue(expr.isLessThan());
    }

    @Test
    public void lessThanGreaterExpression() {
        ComparisonExpression expr = parseExpression("1 <= 1");
        assertTrue(expr.isLessThanOrEqual());
    }

    @Test
    public void greaterThanExpression() {
        ComparisonExpression expr = parseExpression("1 > 1");
        assertTrue(expr.isGreater());
    }

    @Test
    public void greaterThanGreaterExpression() {
        ComparisonExpression expr = parseExpression("1 >= 1");
        assertTrue(expr.isGreaterThanOrEqual());
    }

    @Test
    public void equalityExpression() {
        EqualityExpression expr = parseExpression("1 == 1");
        assertTrue(expr.isEqual());
    }

    @Test
    public void notEqualExpression() {
        EqualityExpression expr = parseExpression("1 != 1");
        assertFalse(expr.isEqual());
    }

}
