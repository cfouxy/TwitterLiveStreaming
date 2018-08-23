package fr.christophe.twitter.client

import java.io.PrintWriter
import java.io.File
import java.util.regex.Pattern

import twitter4j._
import twitter4j.TwitterStreamFactory
import twitter4j.conf.ConfigurationBuilder
import fr.christophe.twitter.Decision.getApplication

class StreamClient {


  def client(): Unit = {

    val configurationBuilder = new ConfigurationBuilder()
      .setOAuthConsumerKey("9do1BHyk2C6MEpq4z5D7Cp7IG")
      .setOAuthConsumerSecret("iNAZIRF1xmjGinUfFMc8EhHohZmXCtttFckYm5mXpYJqdBXEYu")
      .setOAuthAccessToken("819910726982791169-MB4yQz959ayVMXRXlIHGtlRQCT0LEe5")
      .setOAuthAccessTokenSecret("2AYOkNIWoMLMrbClJfUohUYijlXnlTMo9AEQVM2WBerYY")


    val twitterStream = new TwitterStreamFactory(configurationBuilder.build).getInstance

    val outputFileName = getClass.getClassLoader.getResource(".").getFile
    println(s"file : ${outputFileName}")
    val writer = new PrintWriter(new File(outputFileName).getParent + "/statusOutput")

    twitterStream.addListener(new RawStreamListener() {

      override def onMessage(rawJSON: String) = {
        //System.out.println(rawJSON)
        val status: Status  = TwitterObjectFactory.createStatus(rawJSON)
        getApplication(status)

      }

      override def onException(ex: Exception) {
        ex.printStackTrace()
      }
    }).sample()

    writer.close()
  }


}
