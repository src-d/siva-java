import Dependencies._
import de.johoop.jacoco4sbt.XMLReport

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "tech.sourced",
      scalaVersion := "2.11.8",
      version := "0.1.0-SNAPSHOT"
    )),
    name := "siva-java",
    libraryDependencies += scalaTest % Test,
    libraryDependencies += commonsIO
  )

jacoco.settings

jacoco.reportFormats in jacoco.Config := Seq(
  XMLReport(encoding = "utf-8"))