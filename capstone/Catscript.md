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

### Variable Declaration
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


### For loops