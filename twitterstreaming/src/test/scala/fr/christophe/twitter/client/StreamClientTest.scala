package fr.christophe.twitter.client

import org.scalatest.{FlatSpec, Matchers}

class StreamClientTest extends  FlatSpec with Matchers {


  "http://" should "be matched by regex" in {
    val Pattern = ".*(http|https)://(.*)".r

    "http://" match {
      case Pattern(matched) => println(s"HTTP : ${matched}")
      case a => println(s"NO HTTP : $a")
    }
  }
}
