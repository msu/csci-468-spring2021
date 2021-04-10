package edu.montana.csci.csci468.demo;

import java.util.Objects;

public class Classes {

    public static void main(String[] args) {
        Class strClass = String.class;
        boolean stringAssignableFromObject = String.class.isAssignableFrom(Object.class);
        boolean objectAssignableFromString = Object.class.isAssignableFrom(String.class);

        String bar = null;
        Object foo = bar;
    }
}
