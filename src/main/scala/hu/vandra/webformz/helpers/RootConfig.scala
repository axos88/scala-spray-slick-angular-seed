package hu.vandra.webformz.helpers

import com.typesafe.config.ConfigFactory

trait RootConfig {
  val rootConfig = ConfigFactory.load()
}
