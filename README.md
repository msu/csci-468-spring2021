# CSCI 468: Compilers

This is the base upstream repository for CSCI 468, compilers.  It holds the base code for the compiler that we will
write for the class.  The project will be done in pairs.  Note that this class is a capstone class in the CSCI department
and, as such, requires capstone documentation.  An outline of this can be found in the `/capstone` directory.

## Getting Your Repo Set Up

For the project leader, please follow these instructions

- Create a *private* repository in your own account by
    - Going to <https://github.com/new>
    - Enter the name `csci-468-spring2021-private`
    - Select `Private`
    - **DO NOT ADD A README.MD or .gitignore!**
    - Navigate to the `Settings` -> `Manage Access` section
    - Add `1cg` as a collaborator
    - Add your partner as a collaborator

Once your repository is initialized, you and your partner can pull it down to your local machines.  Instructions can
be found on the home page of your new repository.

Next, you and your partner should both add the class repository as an upstream git repo:

```bash
$ git remote add upstream https://github.com/msu/csci-468-spring2021.git
$ git pull upstream main
$ git push
```
This will synchronize your private repository with the class repository.

When you want to get an update from the public class repository you can run this command:

```
$ git pull upstream main
```

## CatScript

In this course we will be creating a small programming language called CatScript

### CatScript Grammar

```ebnf
catscript_program = { program_statement };

program_statement = statement |
                    function_declaration;

statement = for_statement |
            if_statement |
            print_statement |
            variable_statement |
            assignment_statement |
            function_call_statement;

for_statement = 'for', '(', IDENTIFIER, 'in', expression ')', 
                '{', { statement }, '}';

if_statement = 'if', '(', expression, ')', '{', 
                    { statement }, 
               '}' [ 'else', ( if_statement | '{', { statement }, '}' ) ];

print_statement = 'print', '(', expression, ')'

variable_statement = 'var', IDENTIFIER, 
     [':', type_expression, ] '=', expression;

function_call_statement = function_call;

assignment_statement = IDENTIFIER, '=', expression;

function_declaration = 'function', IDENTIFIER, '(', parameter_list, ')' + 
                       [ ':' + type_expression ], '{',  { function_body_statement },  '}';

function_body_statement = statement |
                          return_statement;

parameter_list = [ parameter, {',' parameter } ];

parameter = IDENTIFIER [ , ':', type_expression ];

return_statement = 'return' [, expression];

expression = equality_expression;

equality_expression = comparison_expression { ("!=" | "==") comparison_expression };

comparison_expression = additive_expression { (">" | ">=" | "<" | "<=" ) additive_expression };

additive_expression = factor_expression { ("+" | "-" ) factor_expression };

factor_expression = unary_expression { ("/" | "*" ) unary_expression };

unary_expression = ( "not" | "-" ) unary_expression | primary_expression;

primary_expression = IDENTIFIER | STRING | INTEGER | "true" | "false" | "null"| 
                     list_literal | function_call | "(", expression, ")"

list_literal = '[', expression,  { ',', expression } ']'; 

function_call = IDENTIFIER, '(', argument_list , ')'

argument_list = [ expression , { ',' , expression } ]

type_expression = 'int' | 'string' | 'bool' | 'object' | 'list' [, '<' , type_expression, '>']

```

### CatScript Types

CatScript is statically typed, with a small type system as follows

* int - a 32 bit integer
* string - a java-style string
* bool - a boolean value
* list<x> - a list of value with the type 'x'
* null - the null type 
* object - any type of value

:) :) :)
