package edu.montana.csci.csci468.demo;

import java.util.Arrays;
import java.util.List;

public class Static {
    public static void main(String[] args) {

        List<String> strs = Arrays.asList("foo", "bar");
        List<? extends Object> objects = strs;
        for (Object object : objects) {
            System.out.println(object);
        }
        ((List) objects).add("foo");

//        String[] strs = new String[]{"foo", "bar"};
//        printArray(strs);

//        arrays();
//        dynamicCasting();

    }

    private static void printArray(Object[] objs) {
        for (int i = 0; i < objs.length; i++) {
            Object obj = objs[i];
            System.out.println(obj);
        }
    }

    private static void arrays() {
        String[] strs = new String[]{"foo", "bar"};
        Object[] objects = strs;
        objects[0] = "doh";
        objects[0] = true;
    }

    private static void dynamicCasting() {
        Object x = 10;
        x = "foo";
        x = Arrays.asList(1, 2, 3);
        System.out.println(((String) x).length());
        System.out.println(((List)x).size());
    }

}
