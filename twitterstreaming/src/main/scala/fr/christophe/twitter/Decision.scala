package fr.christophe.twitter

import fr.christophe.twitter.application.{Application, UrlDereferencing, WordCount}
import twitter4j.Status

object Decision {

  def getApplication(status: Status): Application = {

    val Pattern = """.*(http|https):\/\/.*""".r

    status.getText match {
      case Pattern(_) => new UrlDereferencing
      case _ => new WordCount
    }

  }


}
