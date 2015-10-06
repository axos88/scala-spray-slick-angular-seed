package hu.vandra.webformz

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import hu.vandra.webformz.actors.HttpServerActor
import spray.can.Http
import spray.can.server.UHttp

object Boot extends App{
  implicit val system = ActorSystem()

  val handler = system.actorOf(Props(classOf[HttpServerActor]))
  IO(UHttp) ! Http.Bind(handler, interface = "0.0.0.0", port = 8080)
}
