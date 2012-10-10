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
import java.io.{Writer, File}
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
   }
   
   sealed trait Config extends ConfigLike
   
   object ConfigBuilder {
      def apply( config: Config ) : ConfigBuilder = {
         import config._
         val b = new ConfigBuilderImpl
         b.imports = imports
         b.bindings = bindings
         b.out = out
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

      def build : Config = new ConfigImpl( imports, bindings, out )
      override def toString = "MyInterpreter.ConfigBuilder@" + hashCode().toHexString
   }

   private final case class ConfigImpl( imports: Seq[ String ], bindings: Seq[ NamedParam ], out: Option[ Writer ])
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
         override protected def parentClassLoader = MyInterpreter.getClass.getClassLoader

         // note `lastRequest` was added in 2.10
         private def _lastRequest = prevRequestList.last

         def interpretWithResult( line: String, synthetic: Boolean ) : Result = {
            val res0 = interpretWithoutResult( line, synthetic )
            res0 match {
               case Success( name, _ ) => try {
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

//         def interpretWithResult( line: String, synthetic: Boolean, quiet: Boolean ) : Result = {
//            def loadAndRunReq( req: Request ) = {
//               loadAndRun( req ) match {
//                  /** To our displeasure, ConsoleReporter offers only printMessage,
//                   *  which tacks a newline on the end.  Since that breaks all the
//                   *  output checking, we have to take one off to balance.
//                   */
//                  case Right( result ) =>
//                     if( !quiet /* printResults */ && result != null ) {
//                        val resString = result.toString
//                        reporter.printMessage( resString.stripSuffix( "\n" ))
//                     } else if( interpreter.isReplDebug ) { // show quiet-mode activity
//                        val resString = result.toString
//                        reporter.printMessage( resString.trim.lines.map( "[quiet] " + _ ).mkString( "\n" ))
//                     }
//                     // Book-keeping.  Have to record synthetic requests too,
//                     // as they may have been issued for information, e.g. :type
//                     recordRequest( req )
//                     Success( mostRecentVar, result )
//
//                  case Left( failure ) =>
//                     // don't truncate stack traces
//                     reporter.withoutTruncating( reporter.printMessage( failure ))
//                     Error( failure )
//               }
//            }
//
//            if( global == null ) {
//               Error( "Interpreter not initialized" )
//            } else {
//               val reqOption = createRequest( line, synthetic )
//               reqOption match {
//                  case Left( result ) => result
//                  case Right( req )   =>
//                     // null indicates a disallowed statement type; otherwise compile and
//                     // fail if false (implying e.g. a type error)
//                     if( req == null || !req.compile ) {
//                        Error( "Could not compile code" )
//                     } else {
//                        loadAndRunReq(req)
//                     }
//               }
//            }
//         }

//         // FUCK why is this private in IMain ???
//         private def createRequest( line: String, synthetic: Boolean ): Either[ Result, Request ] = {
//            val content = formatting.indentCode( line )
//            val trees = parse( content ) match {
//               case None            => return Left( Incomplete )
//               case Some( Nil )     => return Left( Error( "Parse error" )) // parse error or empty input
//               case Some( _trees )  => _trees
//            }
//
//            // If the last tree is a bare expression, pinpoint where it begins using the
//            // AST node position and snap the line off there.  Rewrite the code embodied
//            // by the last tree as a ValDef instead, so we can access the value.
//            trees.last match {
//               case _: global.Assign => // we don't want to include assignments
//               case _: global.TermTree | _: global.Ident | _: global.Select => // ... but do want other unnamed terms.
//                  val varName = if( synthetic ) naming.freshInternalVarName() else naming.freshUserVarName()
//                  val rewrittenLine = (
//                     // In theory this would come out the same without the 1-specific test, but
//                     // it's a cushion against any more sneaky parse-tree position vs. code mismatches:
//                     // this way such issues will only arise on multiple-statement repl input lines,
//                     // which most people don't use.
//                     if (trees.size == 1) "val " + varName + " =\n" + content
//                     else {
//                        // The position of the last tree
//                        val lastpos0 = earliestPosition( trees.last )
//                        // Oh boy, the parser throws away parens so "(2+2)" is mispositioned,
//                        // with increasingly hard to decipher positions as we move on to "() => 5",
//                        // (x: Int) => x + 1, and more.  So I abandon attempts to finesse and just
//                        // look for semicolons and newlines, which I'm sure is also buggy.
//                        val (raw1, _ /*raw2*/) = content splitAt lastpos0
////                        repldbg("[raw] " + raw1 + "   <--->   " + raw2)
//
//                        val adjustment = (raw1.reverse takeWhile (ch => (ch != ';') && (ch != '\n'))).size
//                        val lastpos = lastpos0 - adjustment
//
//                        // the source code split at the laboriously determined position.
//                        val (l1, l2) = content splitAt lastpos
////                        repldbg("[adj] " + l1 + "   <--->   " + l2)
//
//                        val prefix   = if (l1.trim == "") "" else l1 + ";\n"
//                        // Note to self: val source needs to have this precise structure so that
//                        // error messages print the user-submitted part without the "val res0 = " part.
//                        val combined   = prefix + "val " + varName + " =\n" + l2
//
////                        repldbg(List(
////                           "    line" -> line,
////                           " content" -> content,
////                           "     was" -> l2,
////                           "combined" -> combined) map {
////                              case (label, s) => label + ": '" + s + "'"
////                           } mkString "\n"
////                        )
//                        combined
//                     }
//                  )
//               // Rewriting    "foo ; bar ; 123"
//               // to           "foo ; bar ; val resXX = 123"
//               createRequest( rewrittenLine, synthetic ) match {
//                  case Right( req )  => return Right(req withOriginalLine line)
//                  case x             => return x
//               }
//               case _ =>
//            }
//            Right( new Request( line, trees ))
//         }
//
//         // XXX fuck private
//         private def safePos( t: global.Tree, alt: Int ): Int = try {
//            t.pos.startOrPoint
//         } catch {
//            case _: UnsupportedOperationException => alt
//         }
//
//         // XXX fuck private
//         private def earliestPosition( tree: global.Tree ): Int = {
//            import global._
//            var pos = Int.MaxValue
//            tree foreach { t =>
//               pos = math.min( pos, safePos( t, Int.MaxValue ))
//            }
//            pos
//         }
//
//         private def loadAndRun( req: Request ): Either[ String, Any ] = {
//            if( lineManager == null ) return {
//               try {
//                  Right( req.lineRep call naming.sessionNames.print )
//               }
//               catch {
//                  case ex: Throwable => Left( req.lineRep.bindError( ex ))
//               }
//            }
//            import interpreter.Line._
//
//            try {
//               val execution = lineManager.set( req.originalLine )(
////                  try {
//                     req.lineRep call naming.sessionNames.print
////                  } catch {
////                     case np: NullPointerException => ()
////                  }
//               )
//               execution.await()
//               execution.state match {
////                  case Done       => Right( execution.get() )
//                  case Done       => execution.get(); Right( req.lineRep.call( "$result" ))
//                  case Threw      => Left( req.lineRep.bindError( execution.caught() ))
//                  case Cancelled  => Left( "Execution interrupted by signal.\n" )
//                  case Running    => Left( "Execution still running! Seems impossible." )
//               }
//            }
//            finally {
//               lineManager.clear()
//            }
//         }
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

      override def toString = "MyInterpreter@" + hashCode().toHexString

      def completer: Completion.ScalaCompleter = cmp.completer()

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
    * Interpret a piece of code
    *
    * @param code    the source code to interpret
    * @param quiet   whether to suppress result printing (`true`) or not (`false`)
    *
    * @return        the result of the execution of the interpreted code
    */
   def interpret( code: String, quiet: Boolean = false ) : MyInterpreter.Result

   /**
    * Just as `interpret` but without evaluating the result value. That is, in the case
    * off `Success` the result value will always be `()`.
    */
   def interpretWithoutResult( code: String, quiet: Boolean = false ) : MyInterpreter.Result
   def completer: Completion.ScalaCompleter
}
