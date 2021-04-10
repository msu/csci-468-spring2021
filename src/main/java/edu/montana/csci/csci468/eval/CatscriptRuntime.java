package edu.montana.csci.csci468.eval;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

// TODO - implement proper scoping
public class CatscriptRuntime {
    LinkedList<Map<String, Object>> scopes = new LinkedList<>();

    public CatscriptRuntime(){
        HashMap<String, Object> globalScope = new HashMap<>();
        scopes.push(globalScope);
    }

    public Object getValue(String name) {
        Iterator<Map<String, Object>> mapIterator = scopes.descendingIterator();
        while (mapIterator.hasNext()) {
            Map<String, Object> scope = mapIterator.next();
            if (scope.containsKey(name)) {
                return scope.get(name);
            }
        }
        return null;
    }

    public void setValue(String variableName, Object val) {
        for (Map<String, Object> scope : scopes) {
            if (scope.containsKey(variableName)) {
                scope.put(variableName, val);
                return;
            }
        }
        scopes.peekLast().put(variableName, val);
    }

    public void pushScope() {
        scopes.push(new HashMap<>());
    }

    public void popScope() {
        scopes.pop();
    }

}
