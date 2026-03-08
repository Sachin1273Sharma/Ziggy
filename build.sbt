ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.8.1"
val akkaHttpVersion = "10.5.0"
val circeVersion = "0.14.6"

lazy val root = (project in file("."))
  .settings(
    name := "ziggy",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor-typed" % "2.6.20",
      "ch.qos.logback" % "logback-classic" % "1.2.11",
      "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-stream" % "2.6.20",
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
