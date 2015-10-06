package hu.vandra.webformz.helpers

import shapeless.{HNil, ::}
import spray.http.StatusCodes._
import spray.routing._
import scala.concurrent.Future

trait HelperDirectives extends Directives {
  def ensuring[T](f: =>  Future[T]): Directive1[T] = {
    new Directive1[T] {
      def happly(inner: T :: HNil ⇒ Route) =
        f.value match {
          case Some(scala.util.Success(v)) => inner(v :: HNil)
          case Some(scala.util.Failure(e)) => complete {
            throw e
          }
          case _ => complete {
            ServiceUnavailable
          }
        }
    }
  }
}
