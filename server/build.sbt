organization := "pt.feup.lde"

name := "servidor"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.9.2"

// override def fork = forkRun(new File("target/scala_2.9.2/classes"))

fork := true

libraryDependencies ++= Seq(
   "net.databinder" %% "unfiltered-netty-server" % "0.6.4",
   "net.databinder" %% "dispatch-nio" % "0.8.8",
   "org.clapper" %% "avsl" % "0.4",
   "net.databinder" %% "unfiltered-spec" % "0.6.4" % "test",
   //"com.twitter" % "util-core_2.9.1" % "1.12.8", "com.twitter" % "util-eval_2.9.1" % "1.12.8",
   //"com.twitter" % "util-collection" % "5.3.12",
   "de.sciss" % "scalainterpreterpane_2.9.2" % "1.1.0",
   "jsyntaxpane" % "jsyntaxpane" % "0.9.5-b29" 
)

resolvers ++= Seq(
  "jboss repo" at "http://repository.jboss.org/nexus/content/groups/public-jboss/",
  "twitter-repo" at "http://maven.twttr.com",
  "Clojars Repository" at "http://clojars.org/repo"
)
