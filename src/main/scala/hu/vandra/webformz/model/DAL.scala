package hu.vandra.webformz.model

import slick.driver.JdbcProfile
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import slick.jdbc.meta.MTable

import scala.concurrent.ExecutionContext

trait Profile {
  val profile: JdbcProfile
}

// The data access layer (DAL) is a cake pattern implementation.
// It is an adaptation of the official Slick example set:
// https://github.com/slick/slick-examples (MultiDBCakeExample.scala)
// Understanding that example will help to understand this code.
class DAL(override val profile: JdbcProfile) extends PersonComponent with Profile {
  import profile.api._

  private def createTable[T <: Table[_]](table: TableQuery[T])(implicit ec: ExecutionContext) =
    MTable.getTables.flatMap { tables =>
      if (tables.map(_.name.name).contains(table.baseTableRow.tableName))
        DBIO.successful(())
      else
        table.schema.create
    }

  def create(implicit ec: ExecutionContext) = for {
    _ <- createTable(persons)
  } yield ()

  def drop(implicit ec: ExecutionContext) = for {
    _ <- persons.schema.drop
  } yield ()

  def purge(implicit ec: ExecutionContext) = for {
    _ <- drop
    _ <- create
  } yield ()
}
