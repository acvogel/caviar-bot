package com.signalfire.slack.server

import com.flyberrycapital.slack.SlackClient
import com.flyberrycapital.slack.Responses.PostMessageResponse

import spray.http.FormData

abstract class SlackSlashBot(token: String, name: String, icon_url: String) extends SlackClient(token) {

  def handlePostRequest(formData: SlackSlashFormData): Unit

  val SLEEP_TIME = 1000 

  /** Posting options */
  def opts = Map("username" -> name,
                 "unfurl_media" -> "false",
                 "link_names" -> "1",
                 "icon_url" -> icon_url
                )

  /** Post a tracking url */
  def postMessage(channelID: String,
                  message: String): Option[PostMessageResponse] = {
    var retries = 1
    var response: Option[PostMessageResponse] = None
    do {
      try {
        response = Some(chat.postMessage(channelID, message, opts))
      } catch {
        case e: Exception => 
          retries -= 1
          println(s"Caught exception: ${e.toString}")
          Thread.sleep(SLEEP_TIME)
      }
    } while(!response.isDefined && retries > 0)
    response
  }

}

case class SlackSlashFormData(token: String, team_id: String, team_domain: String, channel_id: String, channel_name: String, user_id: String, user_name: String, command: String, text: String) {
}

object SlackSlashFormData {
  def apply(formData: FormData): SlackSlashFormData = {
    val data = formData.fields.toMap
    new SlackSlashFormData(data("token"), data("team_id"), data("team_domain"), data("channel_id"), data("channel_name"), data("user_id"), data("user_name"), data("command"), data("text"))
  }
}
