package fr.christophe.twitter.application

import fr.christophe.twitter.dao.WordsCountDao._
import org.apache.spark.rdd.RDD
import twitter4j.Status

class WordCount extends Application {

  override def run(statusRDD: RDD[Status]): Unit = {
    val counters = count(statusRDD)
    save(counters)
  }

  private def count(statusRDD: RDD[Status]): RDD[(String, Int)] = {
    statusRDD.flatMap(status =>
      status.getText.split("""\\s+"""))
      .map(word => (word, 1))
      .reduceByKey(_ + _)
  }

  private def save(counters: RDD[(String, Int)]): Unit = {
    counters.foreach(tuple => {
      val wordCounterFound = getCount(tuple._1)
      val wordCounterNew = wordCounterFound.copy(counter = wordCounterFound.counter + tuple._2)
      updateCount(wordCounterNew)
    })
  }
}
