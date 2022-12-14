# Programming-Concepts-2022--Julia-Interpreter
A Java-based interpreter for a simplified version of the Julia language, based off the grammer detailed below. Supports nested if and loop statements.

Grammer, Syntax Analyzer:
* ```<program> → function id ( ) \<block> end```
* ```<block> → <statement> | <statement> <block>```
* ```<statement> → <if_statement> | <assignment_statement> | <while_statement> | <print_statement> | <repeat_statement>```
* ```<if_statement> → if <boolean_expression> then <block> else <block> end```
* ```<while_statement> → while <boolean_expression> do <block> end```
* ```<assignment_statement> -> id <assignment_operator> <arithmetic_expression>```
* ```<repeat_statement> -> repeat <block> until <boolean_expression>```
* ```<print_statement> → print ( <arithmetic_expression> )```
* ```<boolean_expression> → <relative_op> <arithmetic_expression> <arithmetic_expression>```
* ```<relative_op> → le_operator | lt_operator | ge_operator | gt_operator | eq_operator | ne_operator```
* ```<arithmetic_expression> → <id> | <literal_integer> | <arithmetic_op> <arithmetic_expression> <arithmetic_expression>```
* ```<arithmetic_op> → add_operator | sub_operator | mul_operator | div_operator```

Grammer, Lexical Analyzer:
* ```id → letter```
* ```literal_integer → digit literal_integer | digit```
* ```assignment_operator → =```
* ```le_operator → <=```
* ```lt_operator → <```
* ```ge_operator → >=```
* ```gt_operator → >```
* ```eq_operator → ==```
* ```ne_operator → ~=```
* ```add_operator → +```
* ```sub_operator → -```
* ```mul_operator → *```
* ```div_operator → /```
