package com.signalfire.slack

import com.flyberrycapital.slack.{SlackClient, SlackChannel, SlackIM}
import com.flyberrycapital.slack.Responses._

import play.api.libs.json.{Json, JsValue}

import scala.io.Source

case class Restaurant(name: String, text: String, image: String) {
  /** Json representation of Slack API message "attachments" */
  def toSlackAttachment: String = Json.toJson(List(Map("image_url" -> image, "title" -> name, "text" -> text))).toString

  override def toString: String = s"Name: $name Text: $text Image: $image"
}

class CaviarBot(token: String, 
                restaurants: Seq[Restaurant], 
                name: String, 
                icon_url: String) extends SlackClient(token) {
  /** Wait time for failed slack requests */
  val SLEEP_TIME = 1000 

  /** Posting options */
  def opts = Map("username" -> name,
                 "unfurl_media" -> "false",
                 "link_names" -> "1",
                 "icon_url" -> icon_url
                )

  /** Post a cart message to a given channelID (also works for IM IDs) */
  def postCartMessage(channelID: String, 
                      restaurant: Restaurant, 
                      cartUrl: String, 
                      message: String): Option[PostMessageResponse] = {
    val attachments = restaurant.toSlackAttachment
    val cartOpts  = opts + ("attachments" -> attachments)
    val body = s"@channel $message $cartUrl"
    var retries = 5
    var response: Option[PostMessageResponse] = None
    do {
      try {
        response = Some(chat.postMessage(channelID, body, cartOpts))
      } catch {
        case e: Exception => 
          retries -= 1
          println(s"Caught exception: ${e.toString}")
          Thread.sleep(SLEEP_TIME)
      }
    } while(!response.isDefined && retries > 0)
    response
  }

  /** Post a tracking url */
  def postTrackingMessage(channelID: String,
                          trackingUrl: String,
                          message: String): Option[PostMessageResponse] = {
    var retries = 5
    var response: Option[PostMessageResponse] = None
    do {
      try {
        response = Some(chat.postMessage(channelID, s"$message $trackingUrl", opts))
      } catch {
        case e: Exception => 
          retries -= 1
          println(s"Caught exception: ${e.toString}")
          Thread.sleep(SLEEP_TIME)
      }
    } while(!response.isDefined && retries > 0)
    response
  }


  /** Post a tracking url */
  def postMessage(channelID: String,
                  message: String): Option[PostMessageResponse] = {
    var retries = 5
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

  /** Lookup a restaurant by name.
   *
   * @return first restaurant which contains name as a substring
   */
  def findRestaurant(name: String): Option[Restaurant] = {
    restaurants.find(_.name.toLowerCase.contains(name.toLowerCase))
  }
 
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

object CaviarBot {
  def apply(token: String, caviarPagePath: String, name: String, icon_url: String) = {
    val caviarPage = Source.fromFile(caviarPagePath).getLines.mkString
    new CaviarBot(token, 
                  parseCaviarHomepage(caviarPage),
                  name,
                  icon_url)
  }

  // auth url
  // https://slack.com/oauth/authorize?client_id=2525722543.3934415178&state=foo&redirect_uri=http://www.google.com/

  /** Example entries of interest:
   *
   *<li id="merchant-71" data-reactid=".1iurh6blo1s.1.5"><a href="https://www.trycaviar.com/san-francisco/freshroll-soma-71" data-reactid=".1iurh6blo1s.1.5.0"><div class="tile_image" style="background-image:url(https://img.trycaviar.com/RxbMiz19V5GLXma6NXXXMsisnfY=/789x315/https://s3.amazonaws.com/trycaviar.com/offers/71/2596.jpg);" data-reactid=".1iurh6blo1s.1.5.0.0"></div><h4 data-reactid=".1iurh6blo1s.1.5.0.1"><span data-reactid=".1iurh6blo1s.1.5.0.1.1">Freshroll SOMA</span></h4><p data-reactid=".1iurh6blo1s.1.5.0.2">Vietnamese Rolls &amp; Bowls.</p><p class="eta-wrapper" data-reactid=".1iurh6blo1s.1.5.0.3"><span data-reactid=".1iurh6blo1s.1.5.0.3.0"></span><span data-reactid=".1iurh6blo1s.1.5.0.3.1"></span></p></a></li>

    <li id="merchant-27" data-reactid=".13wzu6jvpxc.1.3"><a href="https://www.trycaviar.com/san-francisco/american-grilled-cheese-kitchen--mission-27" data-reactid=".13wzu6jvpxc.1.3.0"><div class="tile_image" style="background-image:url(https://img.trycaviar.com/amy5TtBOFwOu8j4lH45rb4zPoX4=/789x315/https://s3.amazonaws.com/trycaviar.com/offers/27/1135.jpg);" data-reactid=".13wzu6jvpxc.1.3.0.0"><div class="merchant-tile_overlay actionable" data-reactid=".13wzu6jvpxc.1.3.0.0.0"><h4 data-reactid=".13wzu6jvpxc.1.3.0.0.0.0">Pre-Order for Later</h4></div></div><h4 data-reactid=".13wzu6jvpxc.1.3.0.1"><span data-reactid=".13wzu6jvpxc.1.3.0.1.1">American Grilled Cheese Kitchen - Mission</span></h4><p data-reactid=".13wzu6jvpxc.1.3.0.2">Gourmet Comfort.</p><p class="eta-wrapper" data-reactid=".13wzu6jvpxc.1.3.0.3"><span data-reactid=".13wzu6jvpxc.1.3.0.3.0"></span><span class="eta_opening-time" data-reactid=".13wzu6jvpxc.1.3.0.3.1">Wednesday at 7:00pm</span></p></a></li>
   */
  def parseCaviarHomepage(page: String): Seq[Restaurant] = {
    val merchantRegex = """<li id="merchant-\d+".*?>(.*?)</li>""".r
    val imageRegex = """url\(https://img.trycaviar.com/.*?/(http.+?)\)""".r
    val nameRegex = """<h4 .*?><span .*?>(.*?)</span></h4>""".r
    val textRegex = """<p .*?>(.*?)</p>""".r

    def cleanName(name: String) = name.replaceAll("&amp;", "&")

    merchantRegex.findAllIn(page).flatMap { merchantMatch =>
      // Sometimes there are multiple <h4> tags (restaurant open later). The last one is the name.
      val nameMatches = nameRegex.findAllMatchIn(merchantMatch)
      val nameOpt = if (nameMatches.isEmpty) None else Some(nameMatches.toSeq.last)

      val textOpt = textRegex.findFirstMatchIn(merchantMatch)
      val imageOpt = imageRegex.findFirstMatchIn(merchantMatch)
      if (textOpt.isDefined && imageOpt.isDefined && nameOpt.isDefined) {
        Some(Restaurant(cleanName(nameOpt.get.group(1)),
                        cleanName(textOpt.get.group(1)),
                        imageOpt.get.group(1)))
      } else {
        println(s"Error parsing merchant ${merchantMatch.toString}")
        None
      }
    }.toSeq
  }

  case class Config(mode: String = null,
                    restaurantName: String = null, 
                    url: String = null, 
                    cartMessage: String = "cart",
                    trackingMessage: String = "tracking",
                    test: Boolean = false, 
                    channel: String = "food",
                    force: Boolean = false,
                    token: String = null,
                    message: String = null)

  def parser = new scopt.OptionParser[Config]("CaviarBot") {
    head("Caviar SlackBot", "0.2")

    opt[Unit]('t', "test") action { (_, c) => 
      c.copy(test = true) } text("Test mode (post to slackbot)")
    opt[Unit]('f', "force") action { (_, c) =>
      c.copy(force = true) } text("Force post without confirmation")
    opt[String]('c', "channel") action { (x, c) =>
      c.copy(channel = x) } text("Name of the channel to post to")
    opt[String]('p', "token") action { (x, c) =>
      c.copy(token = x) } text("Slack API token (otherwise $SLACK_TOKEN)")

    cmd("cart") action { (_, c) =>
      c.copy(mode = "cart") } text("Post a cart") children(
      arg[String]("<restaurant name>") action { (x, c) =>
        c.copy(restaurantName = x) } text("Name of the restaurant"),
      arg[String]("<url>") action { (x, c) =>
        c.copy(url = x) } text("Caviar cart url"),
      arg[String]("<message>") unbounded() optional() action { (x, c) =>
        c.copy(cartMessage = if(c.cartMessage.equals("cart")) x else c.cartMessage + s" $x") } text("Message text (default: cart)")
    )

    cmd("tracking") action { (_, c) =>
      c.copy(mode = "tracking") } text("Post tracking") children(
      arg[String]("<url>") action { (x, c) =>
        c.copy(url = x) } text("Caviar tracking url"),
      arg[String]("<message>") unbounded() optional() action { (x, c) =>
        c.copy(trackingMessage = if(c.trackingMessage.equals("tracking")) x else c.trackingMessage + s" $x") } text("Message text (default: tracking)")
    )

    cmd("message") action { (_, c) =>
      c.copy(mode = "message") } text("Post a message") children(
      arg[String]("<message>") unbounded() action { (x, c) =>
        c.copy(message = x) } text ("Message text")
    )

    help("help") text("Prints this usage text")
  }

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

  def main(args: Array[String]): Unit = {
    parser.parse(args, Config()) match {
      case Some(config) =>
        val token = getSlackToken(config)
        val bot = CaviarBot(token, 
                            "/Users/adam/Downloads/Caviar _ San Francisco-2.html",
                            "Caviar",
                            "https://pbs.twimg.com/profile_images/553292236109008896/YM2-dI9q.png"
                           )
        val channelID = if (config.test) {
          bot.findIM("uslackbot").get.id
        } else {
          bot.findChannel(config.channel).get.id
        }

        config.mode match {
          case "cart" =>
            val restaurant = bot.findRestaurant(config.restaurantName).getOrElse {
              println(s"Can't find restaurant ${config.restaurantName}")
              System.exit(1)
              null
            }

            val toPost = if (config.force) { 
              true
            } else {
              print(s"Post cart channel: $channelID restaurant: ${restaurant.name} cart: ${config.url} message: '${config.cartMessage}'? [y/N] ")
              val line = io.Source.stdin.getLines.next
              line.startsWith("y")
            } 
            if (toPost) {
              bot.postCartMessage(channelID, restaurant, config.url, config.cartMessage)
            }

          case "tracking" =>    
            val toPost = if (config.force) {
              true
            } else {
              print(s"Post tracking channel: $channelID url: ${config.url} message: '${config.trackingMessage}'? [y/N] ")
              val line = io.Source.stdin.getLines.next
              line.startsWith("y")
            }
            if (toPost) {
              bot.postTrackingMessage(channelID, config.url, config.trackingMessage)
            }
          case "message" =>
            val toPost = if (config.force) {
              true
            } else {
              print(s"Post message channel: $channelID message: '${config.message}'? [y/N] ")
              val line = io.Source.stdin.getLines.next
              line.startsWith("y")
            }
            if (toPost) {
              bot.postMessage(channelID, config.message)
            }
          case _ => println(s"Unknown mode ${config.mode}")
        }
      case None =>
    }
  }
}
