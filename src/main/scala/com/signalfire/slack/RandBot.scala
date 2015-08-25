package com.signalfire.slack

import scala.util.Random

class RandBot(token: String, name: String, icon_url: String) extends SlackSlashBot(token, name, icon_url) {
  def handlePostRequest(formData: SlackSlashFormData): Option[String] = {
    val message = parseDice(formData.text) match {
      case Some((dice, sides)) =>
        val xs = roll(dice, sides)
        s"${formData.user_name} rolled ${dice}d${sides}:\n${xs.mkString(" + ")} = ${xs.sum}"
      case _ => 
        val outcome = roll(1, 2)
        val name = if (outcome.sum == 2) "heads" else "tails"
        s"${formData.user_name} flipped a coin:\n$name"
    }
    postMessage(formData.channel_id, message, opts)
    None
  }

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
