package com.signalfire.slack

import com.signalfire.slack.models._

import com.flyberrycapital.slack.SlackClient
import com.flyberrycapital.slack.Responses.PostMessageResponse

import spray.http.FormData

class CaviarBot(token: String, name: String, icon_url: String) extends SlackSlashBot(token, name, icon_url) {
  
  def handlePostRequest(formData: SlackSlashFormData): Option[String] = {
    val args = parseArgs(formData.text)
    CaviarBot.parser.parse(args, CaviarBot.Config()) match {
      case Some(config) =>
        config.mode match {
          case "cart" =>   
            findRestaurant(config.restaurantName) match {
              case Some(restaurant) =>
                postCartMessage(restaurant, formData.channel_id, config)
                None
              case None =>
                Some(s"""Missing restaurant "${config.restaurantName}"""")
            }
          case "post" =>
            postMessage(formData.channel_id, config.message, opts)
            None
          case _ =>
            Some(s"""Unknown caviar command: "${formData.text}"""")
        }
      case None =>
        Some(s"""Unknown caviar command: "${formData.text}"""")
    }
  }

  /** Split like bash shell, respecting quotes */
  def parseArgs(text: String): Array[String] = {
    """[\""'\u2018].+?[\""'\u2019]|[^ ]+""".r.findAllIn(text).
                                  map(_.replaceAll("""['"\u2018\u2019]""", "")).
                                  toArray
  }

  /** Factored out for testing purposes */
  def findRestaurant(name: String): Option[Restaurant] = Database.findRestaurant(name)

  def postCartMessage(restaurant: Restaurant, channel_id: String,  config: CaviarBot.Config): Option[PostMessageResponse] = {
    val attachments = restaurant.toSlackAttachment
    val cartOpts  = opts + ("attachments" -> attachments)
    val body = s"@channel ${config.message} ${config.url}"
    Some(chat.postMessage(channel_id, body, cartOpts))
  }

}

object CaviarBot {
  case class Config(mode: String = null, restaurantName: String = null, url: String = null, message: String = "")

  def parser = new scopt.OptionParser[Config]("CaviarBot") {
    cmd("cart") action { (_, c) =>
      c.copy(mode = "cart") } text("cart") children(
        arg[String]("<restaurant name>") action { (x, c) =>
          c.copy(restaurantName = x) } text("Name of the restaurant"),
        arg[String]("<url>") action { (x, c) =>
          c.copy(url = x) } text("Caviar cart url"),
        arg[String]("<message>") unbounded() action { (x, c) =>
          c.copy(message = if(c.message.isEmpty) x else c.message + s" $x")})
    
    cmd("post") action { (_, c) =>
      c.copy(mode = "post") } text("Post a message") children(
      arg[String]("<message>") unbounded() optional() action { (x, c) =>
        c.copy(message = if(c.message.isEmpty) x else c.message + s" $x") } text("Message text")
      )
  }
}
