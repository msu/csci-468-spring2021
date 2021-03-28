package edu.montana.csci.csci468.eval;

import edu.montana.csci.csci468.CatscriptTestBase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CatscriptFunctionArgsAndReturnEvalTest extends CatscriptTestBase {
    @Test
    void voidFunctionWorksProperly() {
        assertEquals("10\n", executeProgram("var x = 10\n" +
                "function foo() {}" +
                "foo()" +
                "print(x)"));
    }

    @Test
    void noTypeArgWorksProperly() {
        assertEquals("1\n", executeProgram("function foo(x) { print(x) }" +
                "foo(1)"));
    }

    @Test
    void objectTypeArgWorksProperly() {
        assertEquals("1\n", executeProgram("function foo(x : object) { print(x) }" +
                "foo(1)"));
    }

    @Test
    void intTypeArgWorksProperly() {
        assertEquals("1\n", executeProgram("function foo(x : int) { print(x) }" +
                "foo(1)"));
    }

    @Test
    void booleanTypeArgWorksProperly() {
        assertEquals("true\n", executeProgram("function foo(x : bool) { print(x) }" +
                "foo(true)"));
    }

    @Test
    void stringTypeArgWorksProperly() {
        assertEquals("foo\n", executeProgram("function foo(x : string) { print(x) }" +
                "foo(\"foo\")"));
    }

    @Test
    void listTypeArgWorksProperly() {
        assertEquals("[1, 2, 3]\n", executeProgram("function foo(x : list) { print(x) }" +
                "foo([1, 2, 3])"));
    }

    @Test
    void listTypeWithComponentTypeArgWorksProperly() {
        assertEquals("[1, 2, 3]\n", executeProgram("function foo(x : list<int>) { print(x) }" +
                "foo([1, 2, 3])"));
    }

    @Test
    void mutliArgumentFunctionsWork() {
        assertEquals("1\n", executeProgram("function foo(x, y, z) { print(x) }" +
                "foo(1, 2, 3)"));
        assertEquals("2\n", executeProgram("function foo(x, y, z) { print(y) }" +
                "foo(1, 2, 3)"));
        assertEquals("3\n", executeProgram("function foo(x, y, z) { print(z) }" +
                "foo(1, 2, 3)"));
    }

    @Test
    void returnObjectWorksProperly() {
        assertEquals("1\n", executeProgram("function foo() : object { return 1 }" +
                "print(foo())"));
        assertEquals("true\n", executeProgram("function foo() : object { return true }" +
                "print(foo())"));
        assertEquals("foo\n", executeProgram("function foo() : object { return \"foo\" }" +
                "print(foo())"));
    }

    @Test
    void returnIntWorksProperly() {
        assertEquals("1\n", executeProgram("function foo() : int { return 1 }" +
                "print(foo())"));
    }

    @Test
    void returnBoolWorksProperly() {
        assertEquals("true\n", executeProgram("function foo() : bool { return true }" +
                "print(foo())"));
    }

    @Test
    void returnListWorksProperly() {
        assertEquals("[1, 2, 3]\n", executeProgram("function foo() : list { return [1, 2, 3] }" +
                "print(foo())"));
    }


    @Test
    void returnListWithComponentTypeWorksProperly() {
        assertEquals("[1, 2, 3]\n", executeProgram("function foo() : list<int> { return [1, 2, 3] }" +
                "print(foo())"));
    }


}
