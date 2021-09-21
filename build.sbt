name := "int-test"

version := "0.1"

scalaVersion := "2.13.6"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http-core" % "10.1.12",
  "com.typesafe.akka" %% "akka-http" % "10.1.12",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.12",
  "com.typesafe.akka"  %%  "akka-stream" % "2.6.6"
)