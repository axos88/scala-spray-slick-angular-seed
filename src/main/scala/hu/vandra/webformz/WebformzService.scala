package hu.vandra.webformz

import akka.actor.ActorLogging
import hu.vandra.webformz.actors.{Push, WebSocketWorker}
import hu.vandra.webformz.api.Login
import hu.vandra.webformz.model.{Model, DBConfig, Person}
import hu.vandra.webformz.helpers._
import org.webjars.{MultipleMatchesException, WebJarAssetLocator}
import spray.http.MediaTypes.`text/html`
import spray.http.StatusCodes
import spray.routing.AuthenticationFailedRejection.CredentialsMissing
import spray.routing._
import scala.collection.JavaConverters._


import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps
import scala.util.{Try, Success, Failure}

trait WebformzService extends WebSocketWorker
    with ActorLogging
    with SessionDirectives
    with HelperDirectives
    with WebJarSupport
    with RootConfig {

  val m: Model
  val webJarLocator = new WebJarAssetLocator()

  val sessionKey = SessionKey(rootConfig.getString("swt.key"))

  def mapRejectionsPF(f: PartialFunction[Rejection, Rejection]) =
    mapRejections { rejections =>
      log.warning(rejections.toString())
      rejections.map { r =>
        log.warning(r.toString)
        if (f.isDefinedAt(r)) f(r) else r
      }
    }

  val jsonRoute = {
    import spray.httpx.SprayJsonSupport.sprayJsonMarshaller
    import spray.httpx.SprayJsonSupport.sprayJsonUnmarshaller
    import JsonConverters._

    get {
      path("static") {
        respondWithMediaType(`text/html`) {
          complete {
            <html>
              <body>
                <h1>The <b>S4</b> - <i>Slick Spray Scala Stack</i> is running :-)</h1>
              </body>
            </html>
          }
        }
      }
    } ~
    pathPrefix("api") {
      ensuring(m.readyF) { _ =>
        withSession(sessionKey) { implicit session =>
          path("login") {
            handleRejections(RejectionHandler.Default) {
              entity(as[Login]) { login =>
                completeWithSession {
                  if (login.user == "foo" && login.password == "bar") {
                    session("user") = "foo"
                    session("group") = "admin"
                    StatusCodes.OK
                  }
                  else {
                    StatusCodes.Unauthorized
                  }
                }
              }
            }
          } ~
          pathPrefix("persons") {
            pathEnd {
              get { ctx =>
                ctx.complete(
                  m.getPersons
                )
              }
            } ~
            pathPrefix("new") {
              handleRejections(RejectionHandler.Default) {
                mapRejectionsPF { case MissingSessionKeyRejection(_) => AuthenticationFailedRejection(CredentialsMissing, List()) } {
                  withSessionKey[String]("user") { user =>
                    post {
                      entity(as[Person]) { person =>
                        complete {
                          val f = m.addPerson(person).map { _ => siblingWorkers ! Push("persons", None) }

                          f.map { _ => StatusCodes.Accepted }
                        }
                      }
                    }
                  }
                }
              }
            } ~
            path(IntNumber) { id =>
              handleRejections(RejectionHandler.Default) {
                mapRejectionsPF { case MissingSessionKeyRejection(_) => AuthenticationFailedRejection(CredentialsMissing, List()) } {
                  withSessionKey[String]("user") { user =>
                    delete {
                      complete {
                        val f = m.deletePerson(id).map { _ => siblingWorkers ! Push("persons", None) }

                        f.map { _ => StatusCodes.Accepted }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  val staticRoute = {
    path("favicon.ico") {
      get {
        complete(StatusCodes.NotFound)
      }
    } ~
    path("") {
      get {
        getFromWebJar("webformz", "index.html")
      }
    } ~
    path("config") {
      get {
        getFromResource("application.conf")
      }
    } ~
    path("assets" / Segment) { asset =>
      get {
        getFromWebJar(asset)
      }
    } ~
    path("assets" / Segment / Rest) { (jar, asset) =>
      get {
        getFromWebJar(jar, asset)
      }
    }
  }

  val route = jsonRoute ~ staticRoute
}
