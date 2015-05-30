package com.signalfire.slack.server

import com.flyberrycapital.slack.SlackClient
import com.flyberrycapital.slack.Responses.PostMessageResponse

import spray.http.FormData

class CaviarBot(token: String, name: String, icon_url: String) extends SlackSlashBot(token, name, icon_url) {
  
  def handlePostRequest(formData: SlackSlashFormData) {
    val args = formData.text.split("\\s+") // split here
    CaviarBot.parser.parse(args, CaviarBot.Config()) match {
      case Some(config) =>
        config.mode match {
          case "cart" =>   
            postCartMessage(formData, config)
          case "post" =>
            postMessage(formData.channel_id, config.message, opts)
          case _ =>
            None // TODO error handling
        }
      case None =>
        None 
    }
  }

  def postCartMessage(formData: SlackSlashFormData, config: CaviarBot.Config): Option[PostMessageResponse] = {
    Database.findRestaurant(config.restaurantName) match {
      case Some(restaurant) =>
        val attachments = restaurant.toSlackAttachment
        val cartOpts  = opts + ("attachments" -> attachments)
        val body = s"@channel ${config.message} ${config.url}"
        Some(chat.postMessage(formData.channel_id, body, cartOpts))
      case None => 
        None // error
    }
  }

  /** Post a tracking url */
  def postMessage(channelID: String,
                  message: String,
                  opts: Map[String, String]): Option[PostMessageResponse] = {
    var response: Option[PostMessageResponse] = None
    try {
      response = Some(chat.postMessage(channelID, message, opts))
    } catch {
      case e: Exception => 
        println(s"Caught exception: ${e.toString}")
    }
    response
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
