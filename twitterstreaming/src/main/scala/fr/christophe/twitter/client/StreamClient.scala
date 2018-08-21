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

    def client(): Unit = {

    import twitter4j.conf.ConfigurationBuilder
    val configurationBuilder = new ConfigurationBuilder
    configurationBuilder
      .setOAuthConsumerKey("9do1BHyk2C6MEpq4z5D7Cp7IG")
      .setOAuthConsumerSecret("iNAZIRF1xmjGinUfFMc8EhHohZmXCtttFckYm5mXpYJqdBXEYu")
      .setOAuthAccessToken("819910726982791169-mot5kEAMFkFgp9b7091ov7Yx6c8l4AA")
      .setOAuthAccessTokenSecret("JS1uAWNSRrGZLIrKf8m5v5Ulkb16056nZguv2eja1iB71")

    import twitter4j.TwitterStream
    import twitter4j.TwitterStreamFactory
    val twitterStream = new TwitterStreamFactory(configurationBuilder.build).getInstance


    twitterStream.addListener(
        new StatusListener {
          override def onDeletionNotice(statusDeletionNotice: StatusDeletionNotice): Unit = System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId())

          override def onScrubGeo(userId: Long, upToStatusId: Long): Unit = System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId)

          override def onStatus(status: Status): Unit = System.out.println(status.getText()) // print tweet text to console

          override def onTrackLimitationNotice(numberOfLimitedStatuses: Int): Unit = System.out.println("Got track limitation notice:" + numberOfLimitedStatuses)

          override def onStallWarning(warning: StallWarning): Unit = System.out.println("Got stall warning:" + warning)

          override def onException(e: Exception): Unit = e.printStackTrace()
        })
    twitterStream.sample().
  }


}
