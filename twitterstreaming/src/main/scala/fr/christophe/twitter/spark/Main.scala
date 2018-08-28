package fr.christophe.twitter.spark

import fr.christophe.twitter.Decision
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.twitter._
import org.apache.spark.SparkConf

object Main extends App {

  System.setProperty("twitter4j.oauth.consumerKey", "9do1BHyk2C6MEpq4z5D7Cp7IG")
  System.setProperty("twitter4j.oauth.consumerSecret", "iNAZIRF1xmjGinUfFMc8EhHohZmXCtttFckYm5mXpYJqdBXEYu")
  System.setProperty("twitter4j.oauth.accessToken", "819910726982791169-MB4yQz959ayVMXRXlIHGtlRQCT0LEe5")
  System.setProperty("twitter4j.oauth.accessTokenSecret", "2AYOkNIWoMLMrbClJfUohUYijlXnlTMo9AEQVM2WBerYY")

  implicit val sparkConf = new SparkConf().setMaster("local[*]").setAppName("TwitterLiveStreaming")
  implicit val ssc = new StreamingContext(sparkConf, Seconds(10))

  startStreaming

  ssc.start()
  ssc.awaitTermination()

  def startStreaming() (implicit ssc: StreamingContext) = {

    val stream = TwitterUtils.createStream(ssc, None)

    stream.foreachRDD(
      statusRDD =>
        statusRDD.foreach(
          status => Decision.getApplication(status).run(statusRDD)))

  }

}

