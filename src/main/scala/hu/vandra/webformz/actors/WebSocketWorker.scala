package hu.vandra.webformz.actors

import spray.can.websocket
import spray.can.websocket.frame.TextFrame
import spray.json.{JsNull, JsString, JsObject}
import spray.routing._

final case class Push(channel: String, data: Option[JsObject])

trait WebSocketWorker extends HttpService with websocket.WebSocketServerWorker {

  override def actorRefFactory = context

  override def receive = handshaking orElse runRoute(route) orElse closeLogic

  def route: Route
  def WebSocketReceive: Receive = Map.empty

  def businessLogic = handlePush orElse WebSocketReceive

  def handlePush: Receive = {
    case Push(channel, data) =>
      val jsdata = data match {
        case Some(json) => json.toString()
        case None => ""
      }

      val json = JsObject(
        "channel" -> JsString(channel),
        "data" -> data.getOrElse(JsNull)
      )

      send(TextFrame(json.toString()))
  }

  def siblingWorkers = context.actorSelection("../worker-*")
}
