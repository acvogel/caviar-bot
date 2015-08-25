package com.signalfire.slack.server

/*
import com.flyberrycapital.slack.SlackClient
import com.flyberrycapital.slack.Responses.PostMessageResponse

import spray.http.FormData

import epic.models.{NerSelector, ParserSelector}
import epic.parser.ParserAnnotator
import epic.preprocess
import epic.preprocess.{TreebankTokenizer, MLSentenceSegmenter}
import epic.sequences.{SemiCRF, Segmenter}
import epic.slab.{EntityMention, Token, Sentence}
import epic.trees.{AnnotatedLabel, Tree}
import epic.util.SafeLogging

class NLPBot(token: String, name: String, icon_url: String) extends SlackSlashBot(token, name, icon_url) {

  val parser = ParserSelector.loadParser().get
  val ner = NerSelector.loadNer().get

  def handlePostRequest(formData: SlackSlashFormData): Option[String] = {
    val preprocessed = preprocess.preprocess(formData.text) // wrong.
    val parsed = preprocessed.par.map(parser).seq
    val parseLines = for((tree, sentence) <- parsed zip preprocessed) yield {
      tree render sentence
    }
    val str = s"${parseLines.mkString("\n")}"

    //val nered = preprocessed.par.map(ner.bestSequence(_)).seq  
    //val nerLines = nered.map(_.render)
    //val str = s"${nerLines.mkString("\n")}\n${parseLines.mkString("\n")}"
    postMessage(formData.channel_id, str)
    None
  }
}
*/
