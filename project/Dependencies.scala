import sbt._

object Dependencies {
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.1"
  lazy val scalameter = "com.storm-enroute" %% "scalameter" % "0.8.2"
  lazy val scoverage = "org.scoverage" %% "scalac-scoverage-plugin" % "1.3.1"
  lazy val commonsIO = "commons-io" % "commons-io" % "2.5"
}
