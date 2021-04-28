# Catscript Guide

## Introduction
Catscript is a small statically typed scripting language that compiles to JVM bytecode featuring both evaluation and compilation.
It is designed to take the best features of Java and combine it with features inspired from other languages to create
an ideal language to work with.

Here are some features:
## Features
###Comments
Comments are used in Catscript using the "//" for single line comments and "/* */" for block comments
> /*</br>
> This is</br>
> a multiline comment</br>
> */</br>
> var x = 8</br>
> //This is a single line comment</br>
> print(x)</br>
###Types
Catscript supports basic types for variables. These types can be both explicitly declared and inferred.
Catscript also has a shared namespace, variable names from all fields and sub-scopes are stored in the same location.
Here is the small list of Catscript's type system:
* int - a 32 bit integer
* string - 'Java style' string object
* bool - boolean value
* list - a list of objects
* null - a null type
* object - any value</br>

### For loops
Using the 'in' keyword like most other languages, for loops in Catscript are used to iterate through contents in an array as shown here:
> var list = ["a", "b", "c", "d"] </br>
> for(i in list){</br>
>    print(i)</br>
> }</br>

This returns "abcd". i is assigned to each character within the array and printed as the loop iterates through the array.
### If statements
If statements in Catscript are the same as those in Java. Using the if, else if, and else format, the user uses boolean 
literals or expressions that evaluate to a boolean as well as comparison operators to evaluate if statements.
> var x = "yes"</br>
> if(x == "yes"){</br>
>  print("accepted")</br>
> }</br>
> else if(x == "no"){</br>
>  print("denied")</br>
> } else{</br>
> print("who are you")</br>
> }

The above returns the string "accepted"
### Function Definitions
Functions are defined by including the keyword "function" before your identifier and parameters. The return type is void, 
unless otherwise stated.
#### Explicitly Typed
This function uses explicit types for the parameters and returns an int
> function foo(num: int, truth: bool) : string {</br>
> //do something</br>
> return num</br>
> }
####Type Inferred
This function has a default return type of void. It also uses type inference for the arguments.
> function foo(num, truth){</br>
> //do something</br>
> }</br>
### Unary Expressions
Catscript uses the negate (-) unary operator on integers and booleans take the "not" keyword to negate its value
> var x = 8</br>
> print(-x) //This will print "-8"</br>
> not true  //This evaluates to false
### Comparison
Catscript uses the basic comparison operators less than, less than or equal, greater, greater than or equal, as shown below:
> 1 < 0 //false</br>
> 1 <= 0 //false</br>
> 1 > 0 //true</br>
> 1 >= 0 //true
### Equality
Catscript also check for equality using the basic equality operators on objects of the same type. The operands used are 
"equal equal" and "bang equal"
> 1 == 0 //false</br>
> 1 != 0 //true
### Variable Statements
Variables are declared using the "var" keyword
####Explicitly Typed
You can explicitly define an object's type by including a ": <type>" after var but before your "="
> var num: int = 8</br>
> var hello: string = "Hello World!"</br>
> var truth: bool = false</br>
> var nums: list\<int\> = [1,2,3,4,5]
####Inferred Type
If you don't explicitly give your variable a type, the type will be inferred while the statement is parsed
> var num = 8 //int</br>
> var hello = "Hello World!" //string</br>
> var truth = false //bool</br>
> var nums = [1,2,3,4,5] //list of type int

Catscript also allows for lists of type object with multiple dimensions:
> var list = [8,"Hello World!", [8,40,320]]
### Print Statements
Catscript print statements are similar to those in other languages where the keyword "print" is used to return data to the user. 
> var num = 8</br>
> print(num) //prints out "8"</br>
> print("Hello World!") //prints out "Hello World!"

You can also use the "+" operator to concatenate integers to your strings
> var num = 8</br>
> print("Hello player " + num)</br>
> //returns "Hello player 8"
###Math Operators
Catscript uses basic division and multiplication operators to calculate integers
> var x = 8</br>
> var y = 40</br>
> print(x*y) // returns 320 </br>
> print(x/2) // returns 4

Catscript also uses basic addition and subtraction operators
> print(8+40) //evaluates to 48</br>
> print(8-40) //evaluates to -32</br>
