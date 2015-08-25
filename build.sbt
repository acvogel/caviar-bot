import NativePackagerKeys._
import AssemblyKeys._

packageArchetype.java_application

assemblySettings

test in assembly := {}

mainClass in assembly := Some("com.signalfire.slack.CaviarBot")

//mainClass in assembly := Some("com.signalfire.slack.FacelessBot")

name := "caviar-bot"

mainClass in Compile := Some("com.signalfire.slack.server.Main")

mainClass in (Compile, run) := Some("com.signalfire.slack.server.Main")

resolvers ++= Seq(                                                                  
  "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/",
  "ScalaNLP Maven2" at "http://repo.scalanlp.org/repo",                             
  "Scala Tools Snapshots" at "http://scala-tools.org/repo-snapshots/",              
  Resolver.sonatypeRepo("public"),
  "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"
)                                                                                   

version := "0.1"

scalaVersion := "2.11.2"

crossScalaVersions ++= Seq("2.10.4", "2.11.2")


libraryDependencies ++= { 
  val akkaV = "2.3.9"
  val sprayV = "1.3.3"
  val sprayJsonV = "1.3.2"
  Seq(
    "com.flyberrycapital" %% "scala-slack" % "0.1.1",
    "com.github.scopt" %% "scopt" % "3.3.0",
    "org.scalatest" %% "scalatest" % "2.2.1" % "test",
    "io.spray"            %%  "spray-can"     % sprayV,
    "io.spray"            %%  "spray-json"    % sprayJsonV,
    "io.spray"            %%  "spray-routing" % sprayV,
    "io.spray"            %%  "spray-testkit" % sprayV  % "test",
    "com.typesafe.akka"   %%  "akka-actor"    % akkaV,
    "com.typesafe.akka"   %%  "akka-testkit"  % akkaV   % "test",
    "org.mockito" % "mockito-core" % "1.8.5",
    "joda-time" % "joda-time" % "2.5",
    "org.joda" % "joda-convert" % "1.2",
    "postgresql" % "postgresql" % "9.0-801.jdbc4",
    //"org.scalanlp" %% "epic" % "0.2",
    //"org.scalanlp" %% "epic-parser-en-span" % "2015.1.25",
    //"org.scalanlp" %% "epic-ner-en-conll" % "2015.1.25",
    "junit" % "junit" % "4.5" % "test",                                                                                               
    //"org.scalanlp" %% "breeze" % "0.9",                                                                                               
    //"org.scalanlp" %% "breeze-config" % "0.9.1",                                                                                      
    //"org.scalanlp" %% "nak" % "1.3",                                                                                                  
    //"org.mapdb" % "mapdb" % "0.9.2",                                                                                                  
    //("org.apache.tika" % "tika-parsers" % "1.5").exclude ("edu.ucar", "netcdf").exclude("com.googlecode.mp4parser","isoparser"),      
    //"de.l3s.boilerpipe" % "boilerpipe" % "1.1.0",
    //"net.sourceforge.nekohtml" % "nekohtml" % "1.9.21",//needed by boilerpipe
    //"org.slf4j" % "slf4j-simple" % "1.7.6",
    "org.apache.commons" % "commons-lang3" % "3.3.2",
    "org.seleniumhq.selenium" % "selenium-java" % "2.47.1"
  )
}
