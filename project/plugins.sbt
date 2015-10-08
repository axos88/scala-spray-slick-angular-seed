// Uncomment if you use Revolver:   addSbtPlugin("cc.spray" % "sbt-revolver" % "0.6.1")

resolvers += "spray repo" at "http://repo.spray.io"

resolvers += Resolver.typesafeRepo("releases")

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.6.0")

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.12.0")

addSbtPlugin("io.spray" % "sbt-revolver" % "0.7.2")

addSbtPlugin("com.typesafe.sbt" % "sbt-uglify" % "1.0.3")

addSbtPlugin("net.ground5hark.sbt" % "sbt-css-compress" % "0.1.3")

addSbtPlugin("com.typesafe.sbt" % "sbt-web" % "1.2.2")
