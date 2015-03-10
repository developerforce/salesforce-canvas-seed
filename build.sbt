name := "salesforce-canvas-seed"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.6"

resolvers += "maven-decentral-releases" at "http://maven-decentral.github.io/m2/releases"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  javaWs,
  filters,
  "org.webjars" % "SalesforceCanvasJavascriptSDK" % "29.0",
  "com.salesforce" % "sfdc-canvas-sdk" % "29.0"
)
