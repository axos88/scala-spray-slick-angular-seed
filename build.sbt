import java.io.File
import sbtassembly.MergeStrategy

organization := "vandra.hu"

name := "webformz"

version := "0.0.1"

scalaVersion := "2.11.6"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8", "-feature")

unmanagedResourceDirectories in Compile <++= baseDirectory { base =>
    Seq( base / "src/main/webapp" )
}

resolvers ++= Seq(
  "sonatype releases" at "https://oss.sonatype.org/content/repositories/releases/",
  "typesafe repo" at "http://repo.typesafe.com/typesafe/releases/",
  "spray repo" at "http://repo.spray.io/"
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
)

Revolver.settings.settings

bowerSettings

BowerKeys.frontendDependencies ++= Seq(
  "angular" %%% "=1.2.0-rc.2",
//  "angular-scenario" %%% "=1.2.0-rc.2",
  "angular-route" %%% "=1.2.0-rc.2",
//  "angular-mocks" %%% "=1.2.0-rc.2",
//  "angular-animate" %%% "=1.2.0-rc.2",
//  "angular-cookies" %%% "=1.2.0-rc.2",
//  "angular-resource" %%% "=1.2.0-rc.2",
//  "angular-sanitize" %%% "=1.2.0-rc.2",
//  "angular-touch" %%% "=1.2.0-rc.2",
  "requirejs" %%% "=2.1.8"
//  "requirejs-text" %%% "2.0.10"
)

//BowerKeys.sourceDirectory <<= sourceDirectory (_ / "main" / "webapp" / "lib" )
//BowerKeys.installDirectory <<=  (sourceDirectory in Bower) (_ / "js" / "myStuffGoesHere")

//update <<= update dependsOn (installTask dependsOn pruneTask)


lazy val root = (project in file(".")).enablePlugins(SbtTwirl)
