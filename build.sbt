name := "redis_with_scala"

version := "0.1.0"

scalaVersion := "2.13.13" // or whichever version you are using

// redisclient and its provided dependencies
libraryDependencies ++= Seq(
  "net.debasishg" %% "redisclient" % "3.42",
  "org.slf4j" % "slf4j-api" % "1.7.32",
  "org.slf4j" % "slf4j-log4j12" % "1.7.32",
  "log4j" % "log4j" % "1.2.17"
)

