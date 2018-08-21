package fr.christophe.twitter.client

import scalaj.http._

class StreamClient {


  val baseUrl = "https://stream.twitter.com/"
  val service = "1.1/statuses/sample.json"


  def keepAliveConnetion() = {

    val consumer = Token("key", "secret")

    val headers = Seq(
      "Connection" -> "keep-alive",
      "Accept" -> "application/json",
      "Accept-Encoding" -> "gzip",
      "Accept-Charset" -> "utf-8"
    )
    val consumerToken = Token("819910726982791169-7im5bgoAR2diypOxzHIX47xVepsHYVS","UVgPOrajFWOPVZQWR5tqLfjWHz7BmVXFyBxZEyapLLMRo")
    val token = Token("819910726982791169-mot5kEAMFkFgp9b7091ov7Yx6c8l4AA", "JS1uAWNSRrGZLIrKf8m5v5Ulkb16056nZguv2eja1iB71")
    val response = Http(baseUrl + service)
      .headers(headers)
      .timeout(60000, 30000)
      .oauth(consumerToken, token)
//      .auth("z3hpp8raPPEyjysB8tPYbpfUh", "vntkAUiUtUMa7a4Bw1CSlRGn92ZkBczjW13wFZvxvymKB4rTkG")
      .asString

    println(s"response.code : ${response.code}")
    println(s"response.body : ${response.body}")
  }


}
