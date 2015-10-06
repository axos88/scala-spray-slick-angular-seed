package hu.vandra.webformz.model

import scala.concurrent.ExecutionContext

case class Person(fname: String, lname: String, id: Option[Int] = None)

// It is an adaptation of the official Slick example set (Cake Pattern):
// https://github.com/slick/slick-examples (MultiDBCakeExample.scala)
// Understanding that example will help to understand this code.
trait PersonComponent { this: Profile =>
  import profile.api._

  class Persons(tag: Tag) extends Table[Person](tag, "person") {
    def id = column[Option[Int]]("id", O.PrimaryKey, O.AutoInc)
    def fname = column[String]("fname")
    def lname = column[String]("lname")

    def * = (fname, lname, id) <> (Person.tupled, Person.unapply)
  }

  val persons = TableQuery[Persons]

  private val usersAutoInc = persons.map(u => (u.fname, u.lname)) returning persons.map(_.id) into {
    case (_, id) => id
  }

  def insert(person: Person)(implicit ec: ExecutionContext) = {
    (usersAutoInc += (person.fname, person.lname)).map { id =>
      person.copy(id = id)
    }
  }

  def delete(id: Int) = {
    persons.filter(_.id === id).delete
  }
}
