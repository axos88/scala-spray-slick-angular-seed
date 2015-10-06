package hu.vandra.webformz.actors

import akka.actor.{ActorLogging, Actor}
import hu.vandra.webformz.model.{EC, ProductionDB}
import spray.can.Http

trait ECSetup extends EC { this: Actor =>
  override val ec = context.dispatcher
}

class HttpServerActor extends Actor with ECSetup with ActorLogging with ProductionDB {

  override def receive = {
    // when a new connection comes in we register a WebSocketConnection actor as the per connection handler
    case Http.Connected(remoteAddress, localAddress) =>
      val serverConnection = sender()
      val uid = remoteAddress.toString.drop(1)
      val conn = context.actorOf(HttpWorker.props(serverConnection, m), s"worker-$uid")
      serverConnection ! Http.Register(conn)
  }
}
