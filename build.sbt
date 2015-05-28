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

resolvers += Resolver.sonatypeRepo("public")

resolvers += "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/"

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
    //"com.typesafe.play" %% "play-json" % "2.4.0-M1" % "provided",
    //"com.typesafe.akka" %% "akka-actor" % "2.3.9",
    //"com.typesafe.akka" %% "akka-slf4j" % "2.3.9",
    //"com.typesafe.akka" %% "akka-testkit" % "2.3.9",
    //"com.typesafe.akka" %% "akka-http-core-experimental" % "1.0-M2",
    //"com.typesafe.akka" %% "akka-stream-experimental" % "1.0-M2",
    "io.spray"            %%  "spray-can"     % sprayV,
    "io.spray"            %%  "spray-json"    % sprayJsonV,
    "io.spray"            %%  "spray-routing" % sprayV,
    "io.spray"            %%  "spray-testkit" % sprayV  % "test",
    "com.typesafe.akka"   %%  "akka-actor"    % akkaV,
    "com.typesafe.akka"   %%  "akka-testkit"  % akkaV   % "test",
    "org.specs2"          %%  "specs2-core"   % "2.3.11" % "test",
    "postgresql" % "postgresql" % "9.0-801.jdbc4"
  )
}
