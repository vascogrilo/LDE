organization := "pt.feup.lde"

name := "servidor"

version := "1.0"

scalaVersion := "2.9.2"

fork in run := true

javaOptions in run += "-Xmx1024m"

//import com.typesafe.startscript.StartScriptPlugin

//seq(StartScriptPlugin.startScriptForClassesSettings: _*)

libraryDependencies ++= Seq(
   "net.databinder" %% "unfiltered-netty-server" % "0.6.4",
   "net.databinder" %% "dispatch-nio" % "0.8.8",
   "org.clapper" %% "avsl" % "0.4",
   "net.databinder" %% "unfiltered-spec" % "0.6.4" % "test",
   "de.sciss" % "scalainterpreterpane_2.9.2" % "1.1.0"
)

resolvers ++= Seq(
  "jboss repo" at "http://repository.jboss.org/nexus/content/groups/public-jboss/"
)
