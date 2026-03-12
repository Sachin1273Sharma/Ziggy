ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.8.1"
val circeVersion = "0.14.6"
val pekkoVersion = "1.1.2"
val pekkoHttpVersion = "1.1.0"

lazy val root = (project in file("."))
  .settings(
    name := "ziggy",
	  libraryDependencies ++= Seq(
		  "org.apache.pekko" %% "pekko-actor-typed" % pekkoVersion,
		  "org.apache.pekko" %% "pekko-stream" % pekkoVersion,
		  "org.apache.pekko" %% "pekko-http" % pekkoHttpVersion,

		  "org.apache.pekko" %% "pekko-connectors-kafka" % "1.1.0",
		  "org.apache.kafka" % "kafka-clients" % "3.7.0",

		  "ch.qos.logback" % "logback-classic" % "1.2.11",

		  "redis.clients" % "jedis" % "5.1.0",

		  "io.circe" %% "circe-core"    % circeVersion,
		  "io.circe" %% "circe-generic" % circeVersion,
		  "io.circe" %% "circe-parser"  % circeVersion,

		  "org.postgresql" % "postgresql" % "42.7.10",

		  "com.typesafe.slick" %% "slick" % "3.5.1",
		  "com.typesafe.slick" %% "slick-hikaricp" % "3.5.1",

		  "org.flywaydb" % "flyway-core" % "10.22.0",

		  "com.auth0" % "java-jwt" % "4.4.0",
		  "org.mindrot" % "jbcrypt" % "0.4",

		  "com.typesafe" % "config" % "1.4.3",
		  "com.google.inject" % "guice" % "5.1.0"
	  )
  )
