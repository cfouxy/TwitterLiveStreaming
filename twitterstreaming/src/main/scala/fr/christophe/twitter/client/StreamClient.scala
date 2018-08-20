package fr.christophe.twitter.client

import scalaj.http._

class StreamClient {


  val baseUrl = "https://gnip-stream.twitter.com/"
  val service = "stream/sample10/accounts/:account_name/publishers/twitter/:stream_label.json?partition=1"


  def keepAliveConnetion() = {

    val consumer = Token("key", "secret")
    val headers = Seq(
      "Connection" -> "keep-alive",
      "Accept" -> "application/json",
      "Accept-Encoding" -> "gzip",
      "Accept-Charset" -> "utf-8"
    )
    val consumerToken = Token("819910726982791169-7im5bgoAR2diypOxzHIX47xVepsHYVS","UVgPOrajFWOPVZQWR5tqLfjWHz7BmVXFyBxZEyapLLMRo")
    val response = Http(baseUrl + service)
      .headers(headers)
      .timeout(60000, 30000)
      .oauth(consumerToken)
//      .auth("z3hpp8raPPEyjysB8tPYbpfUh", "vntkAUiUtUMa7a4Bw1CSlRGn92ZkBczjW13wFZvxvymKB4rTkG")
      .asString

    println(s"response.code : ${response.code}")
    println(s"response.body : ${response.body}")
  }


}
