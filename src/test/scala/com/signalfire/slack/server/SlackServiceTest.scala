package com.signalfire.slack.server

import com.signalfire.slack.CaviarBot

import com.flyberrycapital.slack.Responses._
import org.scalatest.FunSuite
import spray.http.StatusCodes._
import spray.testkit.ScalatestRouteTest
import spray.http.FormData

import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._
import org.mockito.Matchers._

class SlackServiceTest extends FunSuite with ScalatestRouteTest with MockitoSugar with SlackService {
  def actorRefFactory = system

  val token = "gIkuvaNzQIHg97ATvDxqgjtO"

  val randbot = mock[CaviarBot]
  when(randbot.postMessage(anyString(), anyString())).thenReturn(Some(PostMessageResponse(true, "", "", null)))

  val caviarbot = mock[CaviarBot]
  when(randbot.postMessage(anyString(), anyString())).thenReturn(Some(PostMessageResponse(true, "", "", null)))

  // so basically, we 
  // 1. check status code of response
  // 2. check that postMessage is called with one of the acceptable messages, to the correct channel


  val team_id = "T0001"
  val team_domain = "example"
  val channel_id = "C2147483705"
  val channel_name = "test"
  val user_id = "U2147483697"
  val user_name = "Steve"

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
      verify(randbot).postMessage(matches(channel_id), matches(answerRegex))
    }
  }

  test ("/rand should roll dice") {
    Post("/rand", slashCommandFormData("rand", "3d4")) ~> slackServiceRoute ~> check {
      assert(status == OK)
      val answerRegex = s"$user_name rolled 3d4:\n[1-4] \\+ [1-4] \\+ [1-4] = ((1[012])|([1-9]))"
      verify(randbot).postMessage(matches(channel_id), matches(answerRegex))
    }
  }
}
