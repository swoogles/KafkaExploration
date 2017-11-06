name := "KafkaExploration"

version := "0.1"

scalaVersion := "2.12.4"

libraryDependencies ++= Seq(
  //  "io.argonaut" %% "argonaut" % "6.2-SNAPSHOT",
//  "io.argonaut" %% "argonaut" % "6.2-RC2" changing(),

//  "com.typesafe.akka" %% "akka-actor" % "2.4.17",
//  "com.lihaoyi" %% "ammonite-ops" % "0.8.2",
"com.typesafe.akka" %% "akka-actor" % "2.5.6",
"org.apache.kafka" %% "kafka" % "1.0.0"
)
