name := "SibalaHTTP"

version := "0.1"

scalaVersion := "2.11.8"

resolvers += "Maven Repository" at "https://repo1.maven.org/maven2/"

libraryDependencies ++= Seq(
	"com.typesafe.akka" % "akka-actor_2.11" % "2.4.7",
	"com.typesafe.akka" % "akka-http-experimental_2.11" % "2.4.7",
	"com.typesafe.akka" % "akka-http-spray-json-experimental_2.11" % "2.4.7",
	"org.scalatest" % "scalatest_2.11" % "2.2.6",
	"com.typesafe.akka" % "akka-http-testkit_2.11" % "2.4.7"
)