package com.signalfire.slack.server

import com.signalfire.slack.CaviarBot

import akka.actor._
import spray.routing._
import spray.json._
import spray.http._
import DefaultJsonProtocol._
import scala.util.Random

class SlackService extends HttpServiceActor with ActorLogging {
  //val token = getSlackToken(config)
  val token = "xoxp-2525722543-2838103605-3934407642-0221ac"

  def randbot = CaviarBot(token, 
                      "resources/Caviar _ San Francisco-2.html",
                      "rand paul",
                      "http://i.imgur.com/k32GPRf.jpg"
                     )

  def caviarbot = CaviarBot(token, 
                      //"resources/Caviar _ San Francisco-2.html",
                      "Caviar",
                      "https://pbs.twimg.com/profile_images/553292236109008896/YM2-dI9q.png"
                     )
                      
  def receive = runRoute {
    path("ping") {
      get {
        complete("PONG")
      }
    } ~
    path("rand") {
      formFields("token", "team_id", "team_domain", "channel_id", "channel_name", "user_id", "user_name", "command", "text") { (token, team_id, team_domain, channel_id, channel_name, user_id, user_name, command, text) =>
        val message = SlackService.parseDice(text) match {
          case Some((dice, sides)) =>
            val xs = SlackService.roll(dice, sides)
            s"$user_name rolled ${dice}d${sides}:\n${xs.mkString(" + ")} = ${xs.sum}"
          case _ => 
            val outcome = SlackService.roll(1, 2)
            val name = if (outcome.sum == 2) "heads" else "tails"
            s"$user_name flipped a coin:\n$name"
        }
        randbot.postMessage(channel_id, message)
        complete(HttpResponse(status = 200))
      }
    } ~
    path("caviar") {
      formFields("token", "team_id", "team_domain", "channel_id", "channel_name", "user_id", "user_name", "command", "text") { (token, team_id, team_domain, channel_id, channel_name, user_id, user_name, command, text) =>
        val args = text.split("""\s+""")
        try {
          val message = caviarbot.slashMain(args, channel_id, user_name)
          if (message.isDefined) {
            complete(s"Error processing command '$command': ${message.get}")
          } else {
            complete(HttpResponse(status = 200))
          }
        } catch {
          case e: Exception =>
            complete(e.getMessage())
        }
      }
    }
  }
}
object SlackService {
  def roll(dice: Int, sides: Int): Seq[Int] = 1 to dice map { _ => Random.nextInt(sides) + 1 }

  // parse 3d5
  def parseDice(command: String): Option[(Int, Int)] = {
    val dicePattern = """(\d+)(?:\s*?)d(?:\s*?)(\d+)""".r
    command.trim match {
      case dicePattern(dice, sides) => Some((dice.toInt, sides.toInt))
      case _ => None
    }
  }
}
