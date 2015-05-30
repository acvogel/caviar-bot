package com.signalfire.slack.server

import play.api.libs.json.{Json, JsValue}

case class Restaurant(id: Int, name: String, text: String, image: String) {
  /** Json representation of Slack API message "attachments" */
  def toSlackAttachment: String = Json.toJson(List(Map("image_url" -> image, "title" -> name, "text" -> text))).toString

  override def toString: String = s"Name: $name Text: $text Image: $image"
}
