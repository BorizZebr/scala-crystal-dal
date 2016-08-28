import sbt.Keys._

name := "scala-crystal-dal"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  // database
  "com.typesafe.slick" %% "slick" % "3.1.1",
  "joda-time" % "joda-time" % "2.9.4",
  "javax.inject" % "javax.inject" % "1",
  "org.scalatest" %% "scalatest" % "2.2.6" % "test",
  "com.h2database" % "h2" % "1.4.192" % "test"
)

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

parallelExecution in Test := false
