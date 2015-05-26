package com.signalfire.slack.server

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import spray.can.Http
import util.Properties

object Main extends App /*with MySslConfiguration*/ {

  implicit val system = ActorSystem()

  // the handler actor replies to incoming HttpRequests
  val handler = system.actorOf(Props[SlackService], name = "handler")

  val myPort = Properties.envOrElse("PORT", "8080").toInt

  //IO(Http) ! Http.Bind(handler, interface = "localhost", port = myPort)
  IO(Http) ! Http.Bind(handler, interface = "0.0.0.0", port = myPort)
}
