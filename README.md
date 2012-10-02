Live Development Environment
=======

A live Scala worksheet for development

Current Status
-----------------

 * Unfiltered netty Server can't yet interpret Scala source code
 * Code editor stable for now. Provides line numbers, syntax highlighting and brackets matching for Scala.

 * Scala server using Unfiltered basic setup. See 'server'. To run the server use 'sbt run' in 'server' folder.
 * First draf of the code editor (front-end) using CodeMirror libraries. See 'editor' folder.
 

How to test the project
-----------------

 * clone the repository
 * under the 'server' folder run the command 'sbt run'
 * open up a new browser at http://localhost:8080/scala
