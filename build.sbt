import AssemblyKeys._

assemblySettings

name := "caviar-bot"

unmanagedBase := baseDirectory.value / "lib"

libraryDependencies += "com.github.scopt" %% "scopt" % "3.3.0" //% "provided"

resolvers += Resolver.sonatypeRepo("public")

test in assembly := {}

version := "0.1"

mainClass in assembly := Some("com.signalfire.slack.CaviarBot")

scalaVersion := "2.11.2"

crossScalaVersions ++= Seq("2.10.4", "2.11.2")

// using managed jar until they answer my pull request
//libraryDependencies += "com.flyberrycapital" %% "scala-slack" % "0.1.0"

libraryDependencies += "com.typesafe.play" %% "play-json" % "2.4.0-M1" % "provided"

mergeStrategy in assembly <<= (mergeStrategy in assembly) { mergeStrategy => {
  case entry => {
    val strategy = mergeStrategy(entry)
    if (strategy == MergeStrategy.deduplicate) MergeStrategy.first
    else strategy
  }
}}
