import sbt._
import sbt.Keys._

object SynergyBuild extends Build {

  lazy val synergy = Project(
    id = "synergy",
    base = file("."),
    settings = Project.defaultSettings ++ Seq(
      name := "synergy",
      organization := "net.caoticode.synergy",
      version := "0.1-SNAPSHOT",
      scalaVersion := "2.10.3",
      
      // dependencies
      libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.2.1"
    )
  )
}
