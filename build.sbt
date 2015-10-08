import WebKeys._

organization := "vandra.hu"

name := "webformz"

version := "0.0.1"

scalaVersion := "2.11.6"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8", "-feature")

resolvers ++= Seq(
  "sonatype releases" at "https://oss.sonatype.org/content/repositories/releases/",
  "typesafe repo" at "http://repo.typesafe.com/typesafe/releases/",
  "spray repo" at "http://repo.spray.io/"
)

val webJarDependencies = Seq(
   "org.webjars.bower" % "angular" % "1.4.7"
  ,"org.webjars.bower" % "angular-route" % "1.4.7"
  ,"org.webjars.bower" % "angular-loader" % "1.4.7"
  ,"org.webjars.bower" % "angular-mocks" % "1.4.7"
  ,"org.webjars.bower" % "angular-websocket" % "1.0.14"
  ,"org.webjars.bower" % "html5-boilerplate-bower" % "4.3.0"
//  ,"org.webjars.bower" % "js-cookie" % ""
  ,"org.webjars.bower" % "bootstrap" % "3.3.5"
  ,"org.webjars.bower" % "jquery" % "2.1.4"
  ,"org.webjars.bower" % "requirejs" % "2.1.20"
)

libraryDependencies ++= Seq(
   "ch.qos.logback"          %   "logback-classic"             % "1.1.1"
  ,"com.h2database"          %   "h2"                          % "1.3.166"
  ,"com.typesafe.akka"       %%  "akka-actor"                  % "2.3.2"
  ,"com.typesafe.slick"      %%  "slick"                       % "3.0.3"
  ,"io.spray"                %%  "spray-json"                  % "1.3.2"
  ,"io.spray"                %%  "spray-can"                   % "1.3.3"
  ,"io.spray"                %%  "spray-routing-shapeless2"    % "1.3.3"
  ,"org.scalatest"           %%  "scalatest"                   % "2.1.5"          % "test"
  ,"postgresql"              %   "postgresql"                  % "9.1-901.jdbc4"
  ,"com.wandoulabs.akka"     %%  "spray-websocket"             % "0.1.4"
  ,"com.nimbusds"            %   "nimbus-jose-jwt"             % "3.10"
  ,"org.webjars"             %   "webjars-locator" % "0.28"
) ++ webJarDependencies

Revolver.settings.settings

pipelineStages := Seq(cssCompress, uglify)

includeFilter in uglify := GlobFilter("*.js")

lazy val root = (project in file(".")).enablePlugins(SbtWeb)

(managedClasspath in Runtime) += (packageBin in Assets).value
