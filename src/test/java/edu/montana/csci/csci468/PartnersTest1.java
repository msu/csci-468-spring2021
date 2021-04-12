package edu.montana.csci.csci468;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PartnersTest1 extends CatscriptTestBase{
    @Test
    void forWorks() {
        assertEquals("Hello\nHello\nHello\nHello\nHello\n", executeProgram(
                "var x = 5" +
                "function loop(arg:int){" +
                "for (i in arg){" +
                        "print(\"Hello\")" +
                        "}" +
                        "}" +
                        "loop(5)"
        ));
    }

    @Test
    void findTheAvg() {
        assertEquals("4", executeProgram(
                "var sum = 0" +
                        "var count = 0" +
                        "var avg = 0" +
                "function average(arg : list<int>): string{" +
                        "for(i in arg){" +
                        "sum = sum + i" +
                        "count = count + 1" +
                        "}" +
                        "avg = sum / count" +
                        "}" +
                        "var list = [2,4,6]" +
                        "print(average(list))"
        ));
    }

    @Test
    void fib(){
        assertEquals("0\n1\n1\n2\n3\n5", executeProgram(
                "var max = 6" +
                        "var prev = 0" +
                        "var next = 1" +
                        "var sum = 0" +
                        "for (i in max){" +
                        "print(prev)" +
                        "sum = prev + next" +
                        "next = sum" +
                "}"
        ));
    }
}
