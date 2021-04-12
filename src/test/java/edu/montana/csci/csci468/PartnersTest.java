package edu.montana.csci.csci468;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PartnersTest extends CatscriptTestBase {

    /**
     * A simple test function to return the max value in an integer list
     */
    @Test
    void findTheMax() {
        assertEquals("6\ndone\n", executeProgram(
                "function foo(arg : list<int>): string{" +
                        "var max = -1" +
                        "for(i in arg){" +
                        "if(max < i){" +
                        "max = i" +
                        "}" +
                        "}" +
                        "print(max)" +
                        "return \"done\"" +
                        "}" +
                        "var mylist = [1,2,3,4,5,6]" +
                        "print(foo(mylist))"));
    }

    /**
     * Recursion test - countdown from 10
     */
    @Test
    void testRecursion() {
        assertEquals("10\n9\n8\n7\n6\n5\n4\n3\n2\n1\n", executeProgram(
                "function recur(arg: int){" +
                        "if(arg > 0){" +
                        "print(arg)" +
                        "recur(arg -1)" +
                        "}" +
                        "}" +
                        "recur(10)"
        ));
    }

    /**
     * Tests whether nested if else statements work properly
     */
    @Test
    void elseIfWorks() {
        assertEquals("-324\n", executeProgram(
                "var x = 10" +
                        "var y = 15" +
                        "var z = -324" +
                        "if(x < z){" +
                        "print(x)" +
                        "}" +
                        "else if(z < x){" +
                        "if(z < y){" +
                        "print(z)" +
                        "}" +
                        "}"
        ));
    }

    /**
     * Recursively called return statements
     */
    @Test
    void recursiveReturn() {
        assertEquals("0\n1\n0\n-3\n-8\n", executeProgram(
                "function foo(n: int): int{" +
                        "if(n <= 1){" +
                        "return n" +
                        "}" +
                        "else{" +
                        "return (foo(n-1) + foo(n-2))" +
                        "}" +
                        "}" +
                        "var mylist = [0,1,2,3,4]" +
                        "for(i in mylist){" +
                        "print(foo(i))" +
                        "}"
        ));
    }

}
