package com.signalfire.slack.server

import com.signalfire.slack.CaviarBot

// service actor should have handlers, which are their own classes
// so we have RandBot
// CaviarBot
// etc.

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
  //def caviarbot = CaviarBot(token, "Caviar", "https://pbs.twimg.com/profile_images/553292236109008896/YM2-dI9q.png")

  def receive = runRoute(slackServiceRoute)
}


trait SlackService extends HttpService {
  def token: String// = sys.env("SLACK_TOKEN")

  //def randbot: CaviarBot// = CaviarBot(token, "wefwef", "rand paul", "http://i.imgur.com/hFPz2fM.jpg")
  def randBot: SlackSlashBot// = CaviarBot(token, "wefwef", "rand paul", "http://i.imgur.com/hFPz2fM.jpg")

  //def caviarbot: CaviarBot //= CaviarBot(token, "Caviar", "https://pbs.twimg.com/profile_images/553292236109008896/YM2-dI9q.png")

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
        //val data = SlackSlashFormData(formData)
        //println("data!!!!: " + data)
        //val message = SlackService.parseDice(text) match {
        //  case Some((dice, sides)) =>
        //    val xs = SlackService.roll(dice, sides)
        //    s"$user_name rolled ${dice}d${sides}:\n${xs.mkString(" + ")} = ${xs.sum}"
        //  case _ => 
        //    val outcome = SlackService.roll(1, 2)
        //    val name = if (outcome.sum == 2) "heads" else "tails"
        //    s"$user_name flipped a coin:\n$name"
        //}
        //randbot.postMessage(channel_id, message)
        complete(HttpResponse(status = 200))
      }
    }// ~
    //path("caviar") {
    //  formFields("token", "team_id", "team_domain", "channel_id", "channel_name", "user_id", "user_name", "command", "text") { (token, team_id, team_domain, channel_id, channel_name, user_id, user_name, command, text) =>
    //    //val args = text.split("""\s+""")
    //    val args = """[\""'].+?[\""']|[^ ]+""".r.findAllIn(text).
    //                                             map(_.replaceAll("""['"]""", "")).
    //                                             toArray
    //    try {
    //      val message = caviarbot.slashMain(args, channel_id, user_name)
    //      if (message.isDefined) {
    //        complete(s"Error processing command '$command': ${message.get}")
    //      } else {
    //        complete(HttpResponse(status = 200))
    //      }
    //    } catch {
    //      case e: Exception =>
    //        complete(e.getMessage())
    //    }
    //  }
    //}
  }
}

//object SlackService {
//  def roll(dice: Int, sides: Int): Seq[Int] = 1 to dice map { _ => Random.nextInt(sides) + 1 }
//
//  // parse 3d5
//  def parseDice(command: String): Option[(Int, Int)] = {
//    val dicePattern = """(\d+)(?:\s*?)d(?:\s*?)(\d+)""".r
//    command.trim match {
//      case dicePattern(dice, sides) => Some((dice.toInt, sides.toInt))
//      case _ => None
//    }
//  }
//}
