package com.signalfire.slack

import com.flyberrycapital.slack.{SlackClient, SlackChannel, SlackIM}
import com.flyberrycapital.slack.Responses._

import play.api.libs.json.{Json, JsValue}

import scala.io.Source

// a girl is not ready to become no one
// first pass: just post, exit.
class FacelessBot(name: String,
                  iconUrl: String,
                  token: String) extends SlackClient(token) {
  val SLEEP_TIME = 1000 

  def post(channelID: String,
           message: String) {
    chat.postMessage(channelID, message, opts)
  }

  def opts = Map("username" -> name, "icon_url" -> iconUrl)

//  def opts = Map("username" -> name,
//                 "unfurl_media" -> "false",
//                 "link_names" -> "1",
//                 "icon_url" -> icon_url
//                )
//

  /** Lookup the channelID string by name, case insensitive exact match */
  def findChannel(name: String): Option[SlackChannel] = {
    val channelsResponse = channels.list()
    if (channelsResponse.ok) {
      channelsResponse.channels.find(_.name.equalsIgnoreCase(name))
    } else {
      None
    }
  }

  /** Find an IM channel by user name of recipient. case insensitive exact match */
  def findIM(name: String): Option[SlackIM] = {
    var retries = 5
    var response: Option[SlackIM] = None
    do {
      try {
        val imsResponse = im.list()
        if (imsResponse.ok) {
          response = imsResponse.ims.find(_.user.equalsIgnoreCase(name))
        } else {
          response = None
        }
      } catch {
        case e: Exception => 
          retries -= 1
          println(s"Caught exception ${e.toString}: Retrying $retries times")
          Thread.sleep(SLEEP_TIME)
      }
    } while(!response.isDefined && retries > 0)
    response
  }
}

object FacelessBot {

  /** Gets slack API token from command line argument, or env $SLACK_TOKEN */
  def getSlackToken(config: Config): String = {
    if (config.token != null) {
      config.token
    } else {
      try {
        sys.env("SLACK_TOKEN")
      } catch {
        case e: Exception => { 
          println("Must provide slack token by argument -p or environment varialbe $SLACK_TOKEN") 
          throw(e)
        }
      }
    }
  }
  

  case class Config(name: String = "",
                    iconUrl: String = "",
                    message: String = "",
                    channel: String = "random",
                    test: Boolean = false,
                    force: Boolean = false,
                    token: String = null)

  def parser = new scopt.OptionParser[Config]("FacelessBot") {
    head("Faceless SlackBot", "0.2")

    opt[Unit]('t', "test") action { (_, c) => 
      c.copy(test = true) } text("Test mode (post to slackbot)")
    opt[String]('c', "channel") action { (x, c) =>
      c.copy(channel = x) } text("Name of the channel to post to")
    opt[String]('p', "token") action { (x, c) =>
      c.copy(token = x) } text("Slack API token (otherwise $SLACK_TOKEN)")
    opt[Unit]('f', "force") action { (_, c) =>
      c.copy(force = true) } text("Force post without confirmation")
     
    arg[String]("<name>") action { (x, c) =>
      c.copy(name = x) } text("name")
    arg[String]("<icon_url>") action { (x, c) =>
      c.copy(iconUrl = x) } text("icon url")
    arg[String]("<message>") unbounded() action { (x, c) =>
      c.copy(message = if (c.message.isEmpty) x else s"${c.message} $x") } text("message text")

    help("help") text("Prints this usage text") 
  }

  // <name> <icon url> <message>
  def main(args: Array[String]): Unit = {
    parser.parse(args, Config()) match {
      case Some(config) =>
        val token = getSlackToken(config)
        val bot = new FacelessBot(config.name, config.iconUrl, token)
        val channelID = if (config.test) {
          bot.findIM("uslackbot").get.id
        } else {
          bot.findChannel(config.channel).get.id
        }
        val toPost = if (config.force) {
          true
        } else {
          print(s"Post channel: $channelID name: ${config.name} icon: ${config.iconUrl} message: '${config.message}'? [y/N] ")
          val line = io.Source.stdin.getLines.next
          line.startsWith("y")
        }
        if (toPost) {
          bot.post(channelID, config.message)
        }
      case None =>
    }
  }
}
