Live Development Environment
=======

A live Scala worksheet for development

Current Status
-----------------

 * New Interface. REPL like. One top area for displaying results and a box down to input one instruction.
 * Apart from Interface improvements, it now supports a new syntax for evaluating the desired code.
   * Provide additional conversion to the instruction, like this: yourinstruction :!: conversion. For example: List(12,22,55) :!: toD3BarChart
   * Provide no aditional conversion and the toHtml default conversion is applied.
 * I'll post later the available conversion option parameters.



 * Live code interpretation is enabled. Code is interpreted every 10sec for now.
 * Conversions are working. So far we have conversions for List,Seq,Map,Int and String.
 * Output is parsed and results identifiers are extracted and stored successfully.
 * Code is interpreted as a whole.
 
 
 * Unfiltered netty Server can't yet interpret Scala source code
 * Code editor stable for now. Provides line numbers, syntax highlighting and brackets matching for Scala.

 * Scala server using Unfiltered basic setup. See 'server'. To run the server use 'sbt run' in 'server' folder.
 * First draf of the code editor (front-end) using CodeMirror libraries. See 'editor' folder.
 

How to test the project
-----------------

 * Clone the repository
 * Under the 'server' folder run the command 'sbt run' or 'sudo sbt run'
 * Open up a new browser at http://localhost:8080/scala
 * Evaluate the code by pressing Ctrl+S
