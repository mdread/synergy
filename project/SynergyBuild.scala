import sbt._
import sbt.Keys._
import com.typesafe.sbteclipse.plugin.EclipsePlugin._

object SynergyBuild extends Build {

  lazy val synergy = Project(
    id = "synergy",
    base = file("."),
    settings = Project.defaultSettings ++ Seq(
      name := "synergy",
      organization := "net.caoticode.synergy",
      version := "0.1-SNAPSHOT",
      scalaVersion := "2.10.3",
      
      EclipseKeys.createSrc := EclipseCreateSrc.Default + EclipseCreateSrc.Resource,
      
      // dependencies
      libraryDependencies += "org.scalatest" % "scalatest_2.10" % "1.9.1" % "test",
      libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.2.1",
      libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % "2.2.1"

    )
  )
}
