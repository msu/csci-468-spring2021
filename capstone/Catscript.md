# Catscript Documentation

## Introduction
Catscript is a statically typed language that features both evaluation and compilation. The Capstript project, including the tokenizer, parser, and bytecode generator were written in Java (JDK 14), and feature JVM targeted bytecode. As Catscript is a JVM targeted language, it features similar behavior and performance as Java itself, while drawing inspiration from languages such as Kotlin. 


## Features
### Type system
Catscript features a small and simple type system as follows</br>
* int - a 32 bit integer
* string - Java style string object
* bool - Boolean value
* list - A list of values of type 'x'
* null - the null type  
* object - Contain any value</br></br>
In addition to these basic types, Catscript features supports explicit types, as well as type inference. Catscript also features a shared namespace, wherein variable names from all fields and sub-scopes are stored in the same location. 


### Comments
Catscript features java-style comments. "//" is used to denote a single line comment, and "/* */" may be used to surround multiline comments.
> var x = 5</br>
> //This is a comment</br>
> print(x)</br>
> /*
> This is</br>
> also a comment
> */

### Variable Declaration
#### Inferred Type
To create a variable in Catscript you must use the 'var' keyword. </br>
Here the variables x,y, and str are created and their types inferred as an integer, boolean, and string respectively.

> var x = 5</br>
> var y = true</br>
> var str = "Catscript!"

Defining a list may be done in the same method with the following syntax:
> var x = [1,2,3,4]</br>

Here x is defined as a list of type int, the type is inferred during statement parsing.  

Catscript also supports object type lists, and multi-dimensional lists.
> var x = [1,2,"foo", [1,5,6]]

#### Explicitly Typed
While Catscript features type inference, you may also explicitly define an object's type. 
The following code accomplishes the same task, yet an explicit type is defined. 
> var x: int = 5</br>
> var y: bool = true</br>
> var str: string = "Catscript!"

You may use explicit type declarations with list objects as well. The following example creates a list of integers. 
> var l: list\<int\> = [1,2,3,4,5]

### Mathematical Operations
#### Addition & Subtraction
Catscript supports basic addition and subtraction between integer types. In addition, string concatenation may be performed if either side of an additive expression is of type string.
> print(5+6) //evaluates to 11</br>
> print(5-6) //evaluates to -1</br>
> print(5+"foo") //evaluates to "5foo"

#### Division and Multiplication
Catscript supports division and multiplication between integer types. 
> var x = 5</br>
> var y = 6</br>
> print(x*y) // evaluates to 30 </br>
> print(x/6) // evaluates to 0

### Unary Operators
Catscript supports two primary unary operators, integer negation and the boolean "not" operator. 
> var x = 5
> print(-x) //print's "-5"</br>
> not false // evaluates to true

### Local Operators
#### Comparison Operators
Catscript supports all primary comparison operators including greater than, less than, greater than or equal, and less than or equal.
> 5 < 6 // evaluates to true</br>
> 5 > 6 //evaluates to false</br>
> 5 <= 6 //evaluates to true</br>
> 5 >= 6 //evaluates to false

#### Equality Operators
In addition the equality between two objects may be checked as long as they are of the same type. Supported operands are "equals equals" and "not equals". 
> 5 == 6 // evaluates to false</br>
> 5 != 6 // evaluates to true


### If Statements
Catscript uses the same syntax as Java to parse for statements. Catscript also supports typical else and else if statements. 
A user may use any comparison operators, boolean literals, or any expression which evaluates to a boolean. </br>
The following example demonstrates basic if statement syntax. 

> var x = 5</br>
> if(x == 4){</br>
>  //true statements</br>
> }</br>
> else if(x == 3){</br>
>  //else if statements</br>
> } else{</br>
> //else statements</br>
> }
### For Loops
For loops may be used to iterate through the contents of any array. Similar to python, Catscript takes advantage of the "in" keyword to facilitate array iteration.

> var x = [1,2,3,4] </br>
> for(i in x){</br>
>    print(i)</br>
> }</br>

The statement above evaluates to "1234", as "i" is assigned to each value contained within x as the iteration proceeds. 

### Function Definitions
A function may be defined by using the keyword "function" followed by an identifier and parameters. Function declaration statements also feature type inference. The return type is assumed to be void, unless defined otherwise following the parameters
#### Type inferred
The following function declaration creates a function named "foo", makes use of type inference for its arguments, and features a default void return type. 
> function foo(myint, mystring, mylist){</br>
> //statements</br>
> }</br>
#### Explicitly Typed
The following function declaration creates the same function, but uses explicit types for its parameters, and defined a return type of string.
> function foo(myint: int, mystring: string, mylist: list\<object\>) : string {</br>
> //statements</br>
> return mystring</br>
> }

### Function Call Expressions
A function may be called by invoking the functions name, and passing in any necessary parameters. The function call statement will then return any object which is returned from the function.
In the following example, the function foo is invoked with the integer value 5. The function then returns the value 5, which is assigned to the variable y. 
> function foo(x: int) : int{</br>
>  return x
> }</br>
> var y = foo(5)


