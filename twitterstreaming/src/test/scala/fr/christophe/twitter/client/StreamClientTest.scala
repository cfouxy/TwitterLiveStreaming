package fr.christophe.twitter.client

import org.scalatest.FlatSpec
import org.scalatest.Matchers

class StreamClientTest extends FlatSpec with Matchers {


  "http" should  "be matched by regex" in {

    val Pattern = """.*(http|https):\/\/.*""".r

    "http://" match {
      case Pattern(matched) => println(s"HTTP : ${matched}")
      case a => println(s"NO HTTP : $a")
    }

    "https://" match {
      case Pattern(matched) => println(s"HTTP : ${matched}")
      case a => println(s"NO HTTP : $a")
    }

    "bla bla https://www.bla.com bla bla " match {
      case Pattern(matched) => println(s"HTTP : ${matched}")
      case a => println(s"NO HTTP : $a")
    }


    "https" match {
      case Pattern(matched) => println(s"HTTP : ${matched}")
      case a => println(s"NO HTTP : $a")
    }
  }
}
