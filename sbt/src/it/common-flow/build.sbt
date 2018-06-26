organization := "org.carlspring.strongbox.examples"

name := "hello-strongbox-sbt"

version := "1.0-SNAPSHOT"

resolvers += "Strongbox" at "http://localhost:48080/storages/storage0/group-releases/"

lazy val publishSettings = Seq(
    publishMavenStyle := true,
    publishTo := {
    val distributionRepository = "http://localhost:48080/storages/storage0/"
      if (isSnapshot.value)
        Some("snapshots" at distributionRepository + "snapshots")
      else
        Some("releases" at distributionRepository + "releases")
    },
    credentials += Credentials("Strongbox Repository Manager", "localhost", "admin", "password")
   )

libraryDependencies ++= Seq(
  "junit" % "junit" % "4.11",
  "ch.qos.logback" % "logback-core" % "1.2.3"
)

mainClass in (Compile, run) := Some("Main")
