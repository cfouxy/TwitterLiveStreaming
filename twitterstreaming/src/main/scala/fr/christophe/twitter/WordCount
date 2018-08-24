package fr.christophe.twitter.application
import org.apache.spark.rdd.RDD
import twitter4j.Status

class WordCount extends Application {

  override def run(statusRDD: RDD[Status]): Unit = {
    super.run(statusRDD)
    statusRDD.map(status =>
    status.getText.split("""\\s+"""))
  }
}
