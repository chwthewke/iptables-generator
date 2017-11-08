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
  .settings( inConfig( Debian )( Seq(
    name := "iptables-generator",
    packageDescription := "This package provides a single executable, iptables-gen, which transforms a set of " +
      "HOCON files into a rules file to be applied by iptables-apply or iptables-restore.",
    packageSummary := "iptables ruleset generator",
    maintainer := "Thomas Dufour <chwthewke@gmail.com>",
    debianPackageDependencies := Seq( "openjdk-8-jre-headless" ),
    debianChangelog := None
  ) ) )
  .enablePlugins( JavaAppPackaging, DebianPlugin, JDebPackaging )

val `iptables-gen-parent` = project
  .in( file( "." ) )
  .settings( sharedSettings )
  .settings( Dependencies.overrides )
  .aggregate( `iptables-gen` )
