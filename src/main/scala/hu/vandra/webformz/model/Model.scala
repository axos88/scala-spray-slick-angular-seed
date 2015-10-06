package hu.vandra.webformz.model

import org.slf4j.LoggerFactory

import scala.concurrent.{ExecutionContext, Future}
import slick.jdbc.JdbcBackend.Database

class Model(name: String, dal: DAL, db: Database)(implicit ec: ExecutionContext) {
  // We only need the DB/session imports outside the DAL
  import dal._
  import dal.profile.api._

  val log = LoggerFactory.getLogger(getClass)

  val readyF =
    createDB
      .map { _ => log.info("DB initialized") }
      .recoverWith { case e => log.error(s"Exception initializing DB: ${e.getClass.getName} ${e.getMessage} ${e.getStackTrace}"); throw e }

  def createDB = db.run(dal.create)

  def dropDB = db.run(dal.drop)

  def purgeDB = db.run(dal.purge)

  def getPersons: Future[Seq[Person]] = {
    db.run(persons.result).map { result =>
      log.info("Got persons: " + result)
      result
    }
  }

  def addPerson(person: Person): Future[Person] = {
    db.run(insert(person)).map { result =>
      log.info("Inserted person: " + result)
      result
    }
  }

  def deletePerson(id: Int): Future[Int] = {
    db.run(delete(id)).map { result =>
      log.info("Deleting person id:" + id)
      result
    }
  }

}
