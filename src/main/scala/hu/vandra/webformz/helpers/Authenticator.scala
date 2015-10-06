package hu.vandra.webformz.helpers

import spray.routing.authentication._
import scala.concurrent.Future

//Mock UserProfile - should probably be a table
case class UserProfile(name: String)

object UserProfile {
  def tryFromUserPass(name: String, password: String): Option[UserProfile] = {
    if (name == "bob" && password == "123")
      Some(UserProfile(s"$name"))
    else
      None
  }
}

object Authenticator extends UserPassAuthenticator[UserProfile] {
  def apply(userPass: Option[UserPass]) = Future.successful(
    userPass match {
      case Some(UserPass(user, pass)) => {
        UserProfile.tryFromUserPass(user, pass)
      }
      case _ => None
    })
}
