package com.signalfire.slack.server

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
  def nlpBot = new NLPBot(token, "NLP", "http://www.startrek.com/legacy_media/images/200307/data03/320x240.jpg")

  def receive = runRoute(slackServiceRoute)
}


trait SlackService extends HttpService {
  def token: String
  def randBot: SlackSlashBot
  def caviarBot: SlackSlashBot
  def nlpBot: SlackSlashBot

  def completeBot(optStr: Option[String]) = {
    optStr match {
      case Some(str) => complete(str)
      case None => complete(HttpResponse(status = 200))
    }
  }

  val slackServiceRoute = {
    path("ping") {
      get {
        complete("PONG")
      }
    } ~
    path("rand") {
      entity(as[FormData]) { formData => 
        completeBot(randBot.handlePostRequest(SlackSlashFormData(formData)))
      }
    } ~
    path("caviar") {
      entity(as[FormData]) { formData =>
        completeBot(caviarBot.handlePostRequest(SlackSlashFormData(formData)))
      }
    } ~
    path("nlp") {
      entity(as[FormData]) { formData =>
        completeBot(nlpBot.handlePostRequest(SlackSlashFormData(formData)))
      }
    }
  }
}
