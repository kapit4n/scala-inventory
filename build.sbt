import com.github.play2war.plugin._
name := """play-scala-intro1"""

version := "1.0.5"

Play2WarPlugin.play2WarSettings

Play2WarKeys.servletVersion := "3.0"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.6"

resolvers += "Scalaz Bintray Repo" at "https://dl.bintray.com/scalaz/releases"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-slick" % "1.1.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "1.1.0",
  "it.innove" % "play2-pdf" % "1.4.0",
  "com.h2database" % "h2" % "1.4.177",
  "mysql" % "mysql-connector-java" % "5.1.36",
  specs2 % Test,
  "be.objectify" %% "deadbolt-scala" % "2.4.1"
)     

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator

wixProductId := "c3458b2d-41d2-4943-826b-cf439b0a1eac"

maintainer := "Luis Arce <luis.arce22@gmail.com>"

packageSummary := "test-windows"

packageDescription := """Test Windows MSI."""

enablePlugins(JavaServerAppPackaging)

fork in run := true
javaOptions in run += "-Dhttp.port=8081"

fork in run := true

fork in run := true



fork in run := true

fork in run := true

fork in run := true

fork in run := true

fork in run := true

fork in run := true

fork in run := true

fork in run := true

fork in run := true