// As of 1.3, SBT uses Coursier instead of Ivy to resolve libraries.
// https://www.scala-sbt.org/1.x/docs/sbt-1.3-Release-Notes.html#Library+management+with+Coursier
// This will break the integration test because Coursier is storing the artifacts in a different path.
// https://get-coursier.io/docs/cache
//
// TODO: Update the project and the test at some point.
//
ThisBuild / useCoursier := false

organization := "org.carlspring.strongbox.examples"

name := "hello-strongbox-sbt"

version := "1.0-SNAPSHOT"

publishMavenStyle := true

credentials += Credentials("Strongbox Repository Manager",
                           "localhost",
                           "admin",
                           "password")

// Do NOT use "resolvers += .." because it APPENDS a repository to the collection which results into false-positive success.
// Artifacts will be resolved from a fallback repository OTHER than the Strongbox which is being tested.
fullResolvers := Seq(
  "Strongbox" at "http://localhost:48080/storages/storage-sbt-proxies/group-sbt-proxies"
)

publishTo := {
  val distributionRepository = "http://localhost:48080/storages/storage0/"
  if (isSnapshot.value)
    Some("snapshots" at distributionRepository + "snapshots")
  else
    Some("releases" at distributionRepository + "releases")
}

libraryDependencies ++= Seq(
  "org.carlspring.commons" % "commons-http" % "1.3",
  "ch.qos.logback" % "logback-core" % "1.2.3"
)

mainClass in (Compile, run) := Some("Main")
