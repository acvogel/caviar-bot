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
  //def randbot = CaviarBot(token, "wefwef", "rand paul", "http://i.imgur.com/hFPz2fM.jpg")
  def randBot = new RandBot(token, "rand paul", "http://i.imgur.com/hFPz2fM.jpg")
  def caviarBot = new CaviarBot(token, "Caviar", "https://pbs.twimg.com/profile_images/553292236109008896/YM2-dI9q.png")

  def receive = runRoute(slackServiceRoute)
}


trait SlackService extends HttpService {
  def token: String// = sys.env("SLACK_TOKEN")

  //def randbot: CaviarBot// = CaviarBot(token, "wefwef", "rand paul", "http://i.imgur.com/hFPz2fM.jpg")
  def randBot: SlackSlashBot// = CaviarBot(token, "wefwef", "rand paul", "http://i.imgur.com/hFPz2fM.jpg")

  def caviarBot: CaviarBot //= CaviarBot(token, "Caviar", "https://pbs.twimg.com/profile_images/553292236109008896/YM2-dI9q.png")

  val slackServiceRoute = {
    path("ping") {
      get {
        complete("PONG")
      }
    } ~
    path("rand") {
      //formFields("token", "team_id", "team_domain", "channel_id", "channel_name", "user_id", "user_name", "command", "text") { (token, team_id, team_domain, channel_id, channel_name, user_id, user_name, command, text) =>
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
    //  formFields("token", "team_id", "team_domain", "channel_id", "channel_name", "user_id", "user_name", "command", "text") { (token, team_id, team_domain, channel_id, channel_name, user_id, user_name, command, text) =>
    //    //val args = text.split("""\s+""")
    //    val args = """[\""'].+?[\""']|[^ ]+""".r.findAllIn(text).
    //                                             map(_.replaceAll("""['"]""", "")).
    //                                             toArray
  }
}
