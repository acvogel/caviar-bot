import AssemblyKeys._

assemblySettings

name := "caviar-bot"


resolvers += Resolver.sonatypeRepo("public")

test in assembly := {}

version := "0.1"

mainClass in assembly := Some("com.signalfire.slack.CaviarBot")

scalaVersion := "2.11.2"

crossScalaVersions ++= Seq("2.10.4", "2.11.2")

libraryDependencies ++= Seq(
  "com.flyberrycapital" %% "scala-slack" % "0.1.1",
  "com.github.scopt" %% "scopt" % "3.3.0",
  "org.scalatest" %% "scalatest" % "2.2.1" % "test",
  "com.typesafe.play" %% "play-json" % "2.4.0-M1" % "provided"
)
