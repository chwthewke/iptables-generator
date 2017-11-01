import sbt._
import sbt.Keys._

// format: off
scalaOrganization in ThisBuild := "org.scala-lang"
scalaVersion      in ThisBuild := "2.12.4"
conflictManager   in ThisBuild := ConflictManager.strict
// format: on

val sharedSettings = Seq( organization := "net.chwthewke" )

val iptablesgenparentSettings =
  Defaults.coreDefaultSettings ++
    sharedSettings ++
    Scalac.settings ++
    Dependencies.settings :+
    (testOptions in Test += Tests.Argument( TestFrameworks.ScalaTest, "-oDF" ))

val `iptables-gen` = project
  .settings( iptablesgenparentSettings )
  .settings( libraryDependencies ++= Dependencies.pureconfig ++ Dependencies.decline )
  .settings( SbtBuildInfo.buildSettings( "IptablesgenBuildInfo" ) )
  .settings( Console.coreImports.settings )
  .settings( mainClass := Some( "net.chwthewke.iptables.Main" ) )
  .enablePlugins( JavaAppPackaging )

val `iptables-gen-parent` = project
  .in( file( "." ) )
  .settings( sharedSettings )
  .settings( Dependencies.overrides )
  .aggregate( `iptables-gen` )
