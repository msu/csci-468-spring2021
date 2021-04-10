package edu.montana.csci.csci468.demo;

import java.util.Arrays;
import java.util.List;

public class BoxingUnboxingDemo {
    public static void main(String[] args) {

        int x = 10;
        Object y = x;
        // x.toString();
        System.out.println(y.toString());

        List<Integer> integers =
                Arrays.asList(1, 2, 3);

        Integer int1 = null;
        int int2 = 10;
        int total = int1 + int2;

    }

    private static Integer getInt1() {
        return true ? null : 10;
    }
}
