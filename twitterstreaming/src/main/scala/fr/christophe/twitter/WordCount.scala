package fr.christophe.twitter.application
import fr.christophe.twitter.dao.WordsCountDao
import org.apache.spark.rdd.RDD
import twitter4j.Status

class WordCount extends Application {

  override def run(statusRDD: RDD[Status]): Unit = {
    super.run(statusRDD)
    val countsRDD = countWords(statusRDD)
    save(countsRDD)
  }


  def countWords(statusRDD: RDD[Status]): RDD[(String, Int)] = {
    statusRDD.flatMap(status => status.getText.split("""\\s+"""))
      .map(word => (word, 1))
      .reduceByKey(_ + _)
  }

  def save(countersRDD: RDD[(String, Int)]): Unit = {

    countersRDD.foreach(tuple =>  {
      val wordCount = WordsCountDao.getCount(tuple._1)
      val wordCountToUpdate = wordCount.copy(counter = wordCount.counter + tuple._2)
      WordsCountDao.updateCount(wordCountToUpdate)
    })

  }
}
