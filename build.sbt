name := "Fuzzy Deduper"

version := "2.0.3"

scalaVersion := "2.9.2"

javaOptions ++= Seq("-Xmx3G", "-Xms900M")

fork := true

libraryDependencies ++= Seq(
    "com.weiglewilczek.slf4s" % "slf4s_2.9.1" % "1.0.7",
	"org.specs2" %% "specs2" % "1.12.1" % "test",
	"org.scalaz" %% "scalaz-core" % "6.0.4" withSources(),
	"com.typesafe" % "config" % "0.4.0"
	)

resolvers ++= Seq("snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
                    "releases"  at "http://oss.sonatype.org/content/repositories/releases")