import io.gatling.sbt.GatlingPlugin

val scala_version = "2.11.4"
val akka_version ="2.3.7"

def gatling = "io.gatling" % "gatling-core" % "2.1.7"
def akkaActor = "com.typesafe.akka" %% "akka-actor" % akka_version
def scalalogging = "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0"
def scalaLibrary = "org.scala-lang" % "scala-library" % scala_version
def highcharts = "io.gatling.highcharts" % "gatling-charts-highcharts" % "2.1.7" % "test"
def gatlingtestframework = "io.gatling" % "gatling-test-framework" % "2.1.7" % "test"
def akkaTest = "com.typesafe.akka" %% "akka-testkit" % akka_version % "test"


lazy val root = (project in file(".")).
  settings(
    organization := "com.abajar",
    version := "0.0.1",
    scalaVersion := scala_version,
    name := "gatling-xmpp-extension",
    libraryDependencies += gatling,
    libraryDependencies += akkaActor,
    libraryDependencies += scalalogging,
    libraryDependencies += highcharts,
    libraryDependencies += gatlingtestframework,
    libraryDependencies += akkaTest,
    libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.6" % "test",
    libraryDependencies += "org.scalamock" %% "scalamock-scalatest-support" % "3.2" % "test",
    libraryDependencies += "org.igniterealtime.smack" % "smack-bosh" % "4.1.8",
    libraryDependencies += "org.igniterealtime.smack" % "smack-extensions" % "4.1.8"
  )

enablePlugins(GatlingPlugin)
