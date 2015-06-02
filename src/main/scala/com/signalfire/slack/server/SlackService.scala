package com.signalfire.slack.server

//import com.signalfire.slack.CaviarBot

import akka.actor._
import spray.routing._
import spray.json._
import spray.http._
import DefaultJsonProtocol._
import scala.util.Random

class SlackServiceActor extends Actor with SlackService {
  def actorRefFactory = context

  val token = sys.env("SLACK_TOKEN")
  def randBot = new RandBot(token, "rand paul", "http://i.imgur.com/hFPz2fM.jpg")
  def caviarBot = new CaviarBot(token, "Caviar", "https://pbs.twimg.com/profile_images/553292236109008896/YM2-dI9q.png")

  def receive = runRoute(slackServiceRoute)
}


trait SlackService extends HttpService {
  def token: String
  def randBot: SlackSlashBot
  def caviarBot: SlackSlashBot

  val slackServiceRoute = {
    path("ping") {
      get {
        complete("PONG")
      }
    } ~
    path("rand") {
      entity(as[FormData]) { formData =>
        randBot.handlePostRequest(SlackSlashFormData(formData))
        complete(HttpResponse(status = 200))
      }
    } ~
    path("caviar") {
      entity(as[FormData]) { formData =>
        caviarBot.handlePostRequest(SlackSlashFormData(formData))
        complete(HttpResponse(status = 200))
      }
    }
  }
}
