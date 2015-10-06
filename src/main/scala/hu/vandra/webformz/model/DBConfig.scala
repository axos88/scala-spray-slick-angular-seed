package hu.vandra.webformz.model

import com.typesafe.config._
import hu.vandra.webformz.helpers.RootConfig

import slick.driver.H2Driver
import slick.driver.PostgresDriver
import slick.jdbc.JdbcBackend.Database

import scala.concurrent.{Promise, ExecutionContext}

sealed trait DBConfig {
  def m: Model
}

trait EC {
  implicit val ec: ExecutionContext
}

trait TestDB extends DBConfig with EC {
  val m = new Model("H2", new DAL(H2Driver),
    Database.forURL("jdbc:h2:mem:servicetestdb", driver = "org.h2.Driver"))
}

trait ProductionDB extends DBConfig with EC with RootConfig {
  val c = rootConfig.getConfig("database")

  val m = new Model("PostgreSQL", new DAL(PostgresDriver),
    Database.forURL(s"jdbc:postgresql://${c.getString("host")}/${c.getString("name")}",
                           driver="org.postgresql.Driver",
                           user = c.getString("user"), password = c.getString("password")))
}
