package edu.montana.csci.csci468.util;

import edu.montana.csci.csci468.parser.CatscriptType;
import edu.montana.csci.csci468.parser.expressions.Expression;

public class Util {

    public static boolean verifyOneOfTypes(Expression expression, CatscriptType... types){
        for (CatscriptType type : types) {
            if (expression.getType() == type){
                return true;
            }
        }
        return false;
    }


}
