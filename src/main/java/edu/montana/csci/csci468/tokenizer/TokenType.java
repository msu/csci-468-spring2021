package edu.montana.csci.csci468.tokenizer;

import java.util.HashMap;
import java.util.Map;

public enum TokenType {
    // syntax
    LEFT_PAREN, RIGHT_PAREN,
    LEFT_BRACE, RIGHT_BRACE,
    LEFT_BRACKET, RIGHT_BRACKET,
    COLON, COMMA, DOT, MINUS, PLUS, SLASH, STAR, MOD,
    BANG_EQUAL,
    EQUAL, EQUAL_EQUAL,
    GREATER, GREATER_EQUAL,
    LESS, LESS_EQUAL,

    // literals
    IDENTIFIER, STRING, INTEGER,

    // keywords
    ELSE, FALSE, FUNCTION, FOR, IF, IN, NOT, NULL,
    PRINT, RETURN, TRUE, VAR, RANGE,
    WHILE, BREAK, CONTINUE, DO, SWITCH, IS, DEFAULT, QUESTION,

    AND, OR,

    ERROR,
    EOF;

    public static final Map<String, TokenType> KEYWORDS = new HashMap<>();

    static {
        KEYWORDS.put("else", ELSE);
        KEYWORDS.put("false", FALSE);
        KEYWORDS.put("function", FUNCTION);
        KEYWORDS.put("for", FOR);
        KEYWORDS.put("in", IN);
        KEYWORDS.put("if", IF);
        KEYWORDS.put("not", NOT);
        KEYWORDS.put("null", NULL);
        KEYWORDS.put("print", PRINT);
        KEYWORDS.put("return", RETURN);
        KEYWORDS.put("true", TRUE);
        KEYWORDS.put("var", VAR);
        KEYWORDS.put("range", RANGE);
        KEYWORDS.put("while", WHILE);
        KEYWORDS.put("break", BREAK);
        KEYWORDS.put("continue", CONTINUE);
        KEYWORDS.put("do", DO);
        KEYWORDS.put("is", IS);
        KEYWORDS.put("switch", SWITCH);
        KEYWORDS.put("default", DEFAULT);
        KEYWORDS.put("and", AND);
        KEYWORDS.put("or", OR);
    }


}
