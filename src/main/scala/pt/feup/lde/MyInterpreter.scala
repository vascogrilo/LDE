/*
 *  Interpreter.scala
 *  (ScalaInterpreterPane)
 *
 *  Copyright (c) 2010-2012 Hanns Holger Rutz. All rights reserved.
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 3 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  For further information, please contact Hanns Holger Rutz at
 *  contact@sciss.de
 */

package pt.feup.lde

import tools.nsc.{Settings => CompilerSettings, ConsoleWriter, NewLinePrintWriter}
import java.io._
import tools.nsc.interpreter.{Results, JLineCompletion, Completion, NamedParam, IMain}
import java.util.concurrent.Executors

object MyInterpreter {
   
   object Config {
      implicit def build( b: ConfigBuilder ) : Config = b.build
      def apply() : ConfigBuilder = new ConfigBuilderImpl
   }
   
   sealed trait ConfigLike {
      implicit def build( b: ConfigBuilder ) : Config = b.build
      def imports: Seq[ String ]
      def bindings: Seq[ NamedParam ]
      def out: Option[ Writer ]
      def ids: Seq[ String ]
   }
   
   sealed trait Config extends ConfigLike
   
   object ConfigBuilder {
      def apply( config: Config ) : ConfigBuilder = {
         import config._
         val b = new ConfigBuilderImpl
         b.imports = imports
         b.bindings = bindings
         b.out = out
         b.ids = ids
         b
      }
   }
   
   sealed trait ConfigBuilder extends ConfigLike {
      def imports: Seq[ String ] // need to restate that to get reassignment sugar
      def imports_=( value: Seq[ String ]) : Unit
      def bindings: Seq[ NamedParam ] // need to restate that to get reassignment sugar
      def bindings_=( value: Seq[ NamedParam ]) : Unit
      def out: Option[ Writer ] // need to restate that to get reassignment sugar
      def out_=( value: Option[ Writer ]) : Unit
	  def ids: Seq[ String ] 
	  def ids_=( value: Seq[ String ]) : Unit
	  
      def build: Config
   }

   sealed trait Result
   case class Success( resultName: String, resultValue: Any ) extends Result
   case class Error( message: String ) extends Result // can't find a way to get the exception right now
   case object Incomplete extends Result

   private final class ConfigBuilderImpl extends ConfigBuilder {
      var imports    = Seq.empty[ String ]
      var bindings   = Seq.empty[ NamedParam ]
      var out        = Option.empty[ Writer ]
	  var ids		 = Seq.empty[ String ]
	  
      def build : Config = new ConfigImpl( imports, bindings, out, ids )
      override def toString = "MyInterpreter.ConfigBuilder@" + hashCode().toHexString
   }

   private final case class ConfigImpl( imports: Seq[ String ], bindings: Seq[ NamedParam ], out: Option[ Writer ], ids: Seq[ String ])
   extends Config {
      override def toString = "MyInterpreter.Config@" + hashCode().toHexString
   }

   def apply( config: Config = Config().build ) : MyInterpreter = {
      val in = makeIMain( config )
      new Impl( in )
   }

   private trait ResultIntp {
      def interpretWithResult(    line: String, synthetic: Boolean = false ) : Result
      def interpretWithoutResult( line: String, synthetic: Boolean = false ) : Result
   }

   private def makeIMain( config: Config ) : IMain with ResultIntp  = {
      val cset = new CompilerSettings()
      cset.classpath.value += File.pathSeparator + System.getProperty( "java.class.path" )
      val in = new IMain( cset, new NewLinePrintWriter( config.out getOrElse (new ConsoleWriter), true )) with ResultIntp {
		  
		 //scala.Console.setOut(new PrintStream(new FileOutputStream("/home/vasco/Desktop/LDE/server/src/main/resources/RESULTS",true),true)) 
		 //scala.Console.setOut( pt.feup.lde.ScalaEditor.output )
		 
         override protected def parentClassLoader = MyInterpreter.getClass.getClassLoader

         // note `lastRequest` was added in 2.10
         private def _lastRequest = prevRequestList.last

         def interpretWithResult( line: String, synthetic: Boolean ) : Result = {
            val res0 = interpretWithoutResult( line, synthetic )
            res0 match {
               case Success( name, _ ) => try {
				  //ids = ids :+ name
                  Success( name, _lastRequest.lineRep.call( "$result" ))
               } catch {
                  case e: Throwable => res0
               }
               case _ => res0
            }
         }

         def interpretWithoutResult( line: String, synthetic: Boolean ) : Result = {
            interpret( line, synthetic ) match {
               case Results.Success => Success( mostRecentVar, () )
               case Results.Error => Error( "Error" ) // doesn't work anymore with 2.10.0-M7: _lastRequest.lineRep.evalCaught.map( _.toString ).getOrElse( "Error" ))
               case Results.Incomplete => Incomplete
            }
         }
     }

      in.setContextClassLoader()
      config.bindings.foreach( in.bind )
      in.addImports( config.imports: _* )
      in
   }

   def async( config: Config = Config().build )( done: MyInterpreter => Unit ) {
      val exec = Executors.newSingleThreadExecutor()
      exec.submit( new Runnable {
         def run() {
            val res = apply( config )
            done( res )
         }
      })
   }

   private final class Impl( in: IMain with ResultIntp ) extends MyInterpreter {
      private val cmp = new JLineCompletion( in )

	  var ids = Seq.empty[ String ]
	  
      override def toString = "MyInterpreter@" + hashCode().toHexString

      def completer: Completion.ScalaCompleter = cmp.completer()
	  
	  def compileString(s : String) = in.compileString(s)

      def interpret( code: String, quiet: Boolean ) : MyInterpreter.Result = {
         if( quiet ) {
            in.beQuietDuring( in.interpretWithResult( code ))
         } else {
            in.interpretWithResult( code )
         }
      }

      def interpretWithoutResult( code: String, quiet: Boolean ) : MyInterpreter.Result = {
         if( quiet ) {
            in.beQuietDuring( in.interpretWithoutResult( code ))
         } else {
            in.interpretWithoutResult( code )
         }
      }
   }
}
trait MyInterpreter {
	
   /**
    * Variable to hold identifiers related to
    * the user's variables and results
    */
    var ids : Seq[ String ]
	
   /**
    * Interpret a piece of code
    *
    * @param code    the source code to interpret
    * @param quiet   whether to suppress result printing (`true`) or not (`false`)
    *
    * @return        the result of the execution of the interpreted code
    */
   def interpret( code: String, quiet: Boolean = false ) : MyInterpreter.Result
   
   def compileString( code: String) : Boolean

   /**
    * Just as `interpret` but without evaluating the result value. That is, in the case
    * off `Success` the result value will always be `()`.
    */
   def interpretWithoutResult( code: String, quiet: Boolean = false ) : MyInterpreter.Result
   def completer: Completion.ScalaCompleter
}
