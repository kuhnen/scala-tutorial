import com.typesafe.sbt.SbtMultiJvm
import com.typesafe.sbt.SbtMultiJvm.MultiJvmKeys.MultiJvm
import com.typesafe.sbt.packager.archetypes.JavaServerAppPackaging
import com.typesafe.sbt.packager.docker.DockerPlugin
import com.zavakid.sbt._
import sbt.Keys._
import sbt._
import spray.revolver.RevolverPlugin._


object MondasBuild extends Build {

  lazy val resourceTestPath = SettingKey[File]("resource test path")
  lazy val resourceMainPath = SettingKey[File]("resource main path")

 val monads = Project(
    id = "monads",
    base = file("monads")) settings(
    resourceDirectory in Test := resourceTestPath.value,
    resourceDirectory in Compile := resourceMainPath.value
    )

  val actors= Project(
    id = "actors",
    base = file("actors"),
    settings = Revolver.settings) settings(
    resourceDirectory in Test := resourceTestPath.value,
    resourceDirectory in Compile := resourceMainPath.value
    ) enablePlugins(SbtOneLog)


  override lazy val settings = super.settings ++ Seq(
    organization := "com.github.kuhnen",
    name := "tutorial",
    version := "0.1",
    scalaVersion := "2.11.5",
    resolvers ++= Dependencies.resolvers,
    libraryDependencies ++= Dependencies.libraryDependenciesCore ++ Dependencies.test,
    scalacOptions ++= Seq(
      "-unchecked",
      "-deprecation",
      "-Xlint",
      "-Ywarn-dead-code",
      "-language:_",
      "-target:jvm-1.7",
      "-encoding", "UTF-8",
      "-Yclosure-elim",
      //"-Xfatal-warnings",
      "-Yinline",
      "-Xverify",
      "-feature"
    ),
    javaOptions in test += s"""-Djava.library.path=${baseDirectory.value}/hyperic-sigar-1.6.4/sigar-bin/lib""",
    resourceTestPath := baseDirectory.value / "src" / "test" / "resources",
    resourceMainPath := baseDirectory.value / "src" / "main" / "resources",
    fork in test := true,
    fork in run := true
  )

}



object Dependencies {

  import Version._

  lazy val libraryDependenciesCore = Seq(
    "com.typesafe.akka" %% "akka-actor" % akka,
    "com.typesafe.akka" %% "akka-slf4j" % akka,
    "com.typesafe.akka" %% "akka-cluster" % akka,
    "com.typesafe.akka" %% "akka-contrib" % akka,
    "io.spray" %% "spray-can" % spray,
    "io.spray" %% "spray-util" % spray,
    "io.spray" %% "spray-routing" % spray,
    "io.spray" %% "spray-client" % spray,
    "io.spray" %% "spray-http" % spray,
    "io.spray" %% "spray-httpx" % spray,
    "org.json4s" %% "json4s-native" % json,
    "org.json4s" %% "json4s-ext" % json,
    "com.sksamuel.elastic4s" %% "elastic4s" % esDsl,
    "io.kamon" %% "kamon-core" % kamon,
    "io.kamon" %% "kamon-spray" % kamon,
    "io.kamon" %% "kamon-system-metrics" % kamon,
    "io.kamon" %% "kamon-statsd" % kamon,
    "io.kamon" %% "kamon-log-reporter" % kamon,
     "com.github.scopt" %% "scopt" % "3.3.0",
    "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
    "ch.qos.logback" % "logback-classic" % "1.1.2",
    "com.github.nscala-time" %% "nscala-time" % nscalaTime,
    "com.typesafe" % "config" % "1.2.1",
    "org.aspectj" % "aspectjweaver" % jweaver,
    "com.101tec" % "zkclient" % "0.4" intransitive(),
    "io.kamon" % "sigar-loader" % "1.6.5-rev001",
    "org.fusesource" % "sigar" % "1.6.4" classifier ("native") classifier (""),
    "com.github.detro" % "phantomjsdriver" % "1.2.0",
    "com.google.code.findbugs" % "jsr305" % "3.0.0"

  )

  lazy val test = Seq(
    "com.typesafe.akka" %% "akka-testkit" % akka,
    "com.typesafe.akka" %% "akka-multi-node-testkit" % akka,
    "io.spray" %% "spray-testkit" % spray % "test",
    "org.scalamock" %% "scalamock-scalatest-support" % "3.2.1" exclude("org.scalatest", "scalatest_2.11"),
    "org.scalatest" %% "scalatest" % scalaTest //% "test"
  )

  lazy val resolvers = Seq(
    "spray repo" at "http://repo.spray.io",
    "spray nightlies" at "http://nightlies.spray.io",
    Resolver.sonatypeRepo("releases"),
    "Kamon Repository" at "http://repo.kamon.io",
    "Sonatype Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/",
    "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
    "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/",
    "Websudos releases" at "http://maven.websudos.co.uk/ext-release-local",
     Resolver.sonatypeRepo("public")
  )


}

object Version {

  val akka = "2.3.9"

  val spray = "1.3.2"

  val kamon = "0.3.5"

  val kafka = "0.8.2-beta"

  val zookeeper = "3.3.4"

  val json = "3.2.11"

  val nscalaTime = "1.8.0"

  val scalaTest = "2.2.4"

  val jweaver = "1.8.5"

  val esDsl = "1.4.13"

}
