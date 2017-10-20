import Dependencies.{scalaTest, _}
import sbt.Keys.libraryDependencies

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "tech.sourced",
      scalaVersion := "2.11.11",
      version := "0.1.0-SNAPSHOT"
    )),
    name := "siva-java",
    libraryDependencies += scalaTest % Test,
    libraryDependencies += scoverage % Test,
    libraryDependencies += commonsIO % Test
  )

parallelExecution in Test := false
logBuffered in Test := false
