name := "pbt"

version := "1.0"

scalaVersion := "2.11.8"

/**
  * goto https://github.com/databricks/learning-spark/blob/master/build.sbt
  * for list of common dependencies
  */


/**
  * akka
  */
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.0.6",
  "com.typesafe.akka" %% "akka-actor" % "2.5.2",
  "com.typesafe.akka" %% "akka-stream" % "2.5.2"
)

/**
  * ScalaTest
  */
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test"


/**
  * Spark
  */
libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "2.1.1",
  "org.apache.spark" %% "spark-sql" % "2.1.1",
  "org.apache.spark" %% "spark-streaming" % "2.1.1",
  "org.apache.spark" %% "spark-hive" % "2.1.1",
  "org.apache.spark" %% "spark-mllib" % "2.1.1"
)
