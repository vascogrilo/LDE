Live Development Environment
=======

A live Scala worksheet for development

Current Status
-----------------

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
