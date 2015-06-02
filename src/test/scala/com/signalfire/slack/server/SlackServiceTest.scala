package com.signalfire.slack.server

import com.flyberrycapital.slack.Responses._
import org.scalatest.FunSuite
import spray.http.StatusCodes._
import spray.testkit.ScalatestRouteTest
import spray.http.FormData

import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._
import org.mockito.Matchers._

import org.joda.time.DateTime

class SlackServiceTest extends FunSuite with ScalatestRouteTest with MockitoSugar with SlackService {
  def actorRefFactory = system

  val token = "gIkuvaNzQIHg97ATvDxqgjtO"
  val team_id = "T0001"
  val team_domain = "example"
  val channel_id = "C2147483705"
  val channel_name = "test"
  val user_id = "U2147483697"
  val user_name = "Steve"
  val restaurant = Restaurant(0, "Cha Cha Cha", "", "")

  val randBot = spy(new RandBot(token, "", ""))
  doReturn(Some(PostMessageResponse(true, "", "", DateTime.now))).when(randBot).postMessage(anyString, anyString)

  val caviarBot = spy(new CaviarBot(token, "", ""))
  doReturn(Some(restaurant)).when(caviarBot).findRestaurant("cha cha")
  doReturn(Some(PostMessageResponse(true, "", "", DateTime.now))).when(caviarBot).postCartMessage(restaurant, channel_id, CaviarBot.Config("cart", "cha cha", "www.google.com", "lunch cart"))


  /** Make a post request for a Slack slash command */
  def slashCommandFormData(command: String, text: String): FormData = {
    FormData(Seq("command" -> command, "text" -> text, "token" -> token, "team_id" -> team_id, "team_domain" -> team_domain, 
                 "channel_id" -> channel_id, "channel_name" -> channel_name, "user_id" -> user_id, "user_name" -> user_name))
  }

  test("/ping should pong") {
    Get("/ping") ~> slackServiceRoute ~> check {
      assert(status == OK)
      assert(entity.asString == "PONG")
    }
  }

  test("/rand should flip coins") {
    Post("/rand", slashCommandFormData("rand", "")) ~> slackServiceRoute ~> check {
      assert(status == OK)
      val answerRegex = s"$user_name flipped a coin:\n(heads|tails)"
      verify(randBot).postMessage(matches(channel_id), matches(answerRegex))
    }
  }

  test("/rand should roll dice") {
    Post("/rand", slashCommandFormData("rand", "3d4")) ~> slackServiceRoute ~> check {
      assert(status == OK)
      val answerRegex = s"$user_name rolled 3d4:\n[1-4] \\+ [1-4] \\+ [1-4] = ((1[012])|([1-9]))"
      verify(randBot).postMessage(matches(channel_id), matches(answerRegex))
    }
  }

  test("/caviar should post messages") {
    val message = "my message for posting"
    Post("/caviar", slashCommandFormData("caviar", s"post $message")) ~> slackServiceRoute ~> check {
      verify(caviarBot).postMessage(matches(channel_id), matches(message), anyObject.asInstanceOf[Map[String, String]])
      assert(status == OK)
    }
  }

  test("/caviar should post carts") {
    val restaurantName = "'cha cha'"
    val url = "www.google.com"
    val message = "lunch cart"
    Post("/caviar", slashCommandFormData("caviar", s"cart $restaurantName $url $message")) ~> slackServiceRoute ~> check {
      verify(caviarBot).postCartMessage(restaurant, channel_id, CaviarBot.Config("cart", "cha cha", "www.google.com", "lunch cart"))
      assert(status == OK)
    }
  }
}
