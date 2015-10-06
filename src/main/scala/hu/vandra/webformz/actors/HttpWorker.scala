package hu.vandra.webformz.actors

import akka.actor.{Props, ActorRef}
import hu.vandra.webformz.WebformzService
import hu.vandra.webformz.model.{Model, ProductionDB}

object HttpWorker {
  def props(serverConnection: ActorRef, m: Model) = Props(classOf[HttpWorker], serverConnection, m)
}

class HttpWorker(val serverConnection: ActorRef, val m: Model) extends WebformzService
