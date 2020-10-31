name := """youtube-analyzer"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.13.1"

libraryDependencies += guice
libraryDependencies += javaWs
libraryDependencies += javaForms
libraryDependencies += "org.mockito" % "mockito-core" % "3.6.0" % Test
