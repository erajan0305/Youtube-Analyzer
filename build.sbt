name := """youtube-analyzer"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.13.1"

lazy val akkaVersion = "2.5.16"

libraryDependencies += guice
libraryDependencies += javaWs
libraryDependencies += javaForms
libraryDependencies += "org.mockito" % "mockito-core" % "3.6.0" % Test
libraryDependencies += "com.vdurmont" % "emoji-java" % "4.0.0"
libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test
libraryDependencies += "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion % Test
