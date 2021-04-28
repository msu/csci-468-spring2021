package edu.montana.csci.csci468;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.montana.csci.csci468.parser.expressions.*;
import edu.montana.csci.csci468.parser.statements.CatScriptProgram;

public class PartnersTest1 extends CatscriptTestBase {
    @Test
    void forWorks() {
        assertEquals("Hello\nHello\nHello\nHello\nHello\n", executeProgram(
                "var arg = [1, 2, 3, 4, 5]" +
                        "function loop(x:list<int>){" +
                        "for (i in x){" +
                        "print(\"Hello\")" +
                        "}" +
                        "}" +
                        "loop(arg)"
        ));
    }

    @Test
    void findTheAvg() {
        assertEquals("4\n", executeProgram(
                "var sum= 0\n" +
                        "var count = 0\n" +
                        "var avg = 0\n" +
                        "function average(arg : list<int>){\n" +
                        "for(i in arg){\n" +
                        "sum = sum + i\n" +
                        "count = count + 1\n" +
                        "}\n" +
                        "avg = sum / count\n" +
                        "print(avg)\n" +
                        "}\n" +
                        "var list = [2,4,6]\n" +
                        "average(list)\n"
        ));
    }

    @Test
    void ifElseIfElse() {
        assertEquals("Hello World\n", executeProgram(
                "var x = 8" +
                        "if(x < 5){" +
                        "print(\"No way\")" +
                        "}" +
                        "else if(x == 5){" +
                        "print(\"Jose\")" +
                        "} else {" +
                        "print(\"Hello World\")" +
                        "}"
        ));
    }
}
