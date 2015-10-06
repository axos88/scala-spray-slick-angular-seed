package hu.vandra.webformz.helpers

import hu.vandra.webformz.api.Login
import hu.vandra.webformz.model.Person
import spray.json.DefaultJsonProtocol

object JsonConverters extends DefaultJsonProtocol {
  implicit val impPerson = jsonFormat3(Person)
  implicit val impLogin = jsonFormat2(Login)
}
