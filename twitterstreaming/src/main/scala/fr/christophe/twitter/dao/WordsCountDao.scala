package fr.christophe.twitter.dao

import java.sql.ResultSet

import scala.util.control.Exception.allCatch

case class WordCount(id: Option[BigInt], word: String, counter: Int)

object WordsCountDao {



  def getCount(word: String): WordCount = {

    val resultSet = PostgreSQLConnection.executeSelect(
      s"SELECT * from tweets.WORD_COUNTERS where word = $word"
    )
    allCatch opt getResult(resultSet).head getOrElse WordCount(None, word = word, counter = 0)
  }

  def updateCount(wordCount: WordCount): Unit = {

    if (wordCount.id.isDefined) {
      PostgreSQLConnection.executeUpdate(
        s"UPDATE tweets.WORD_COUNTERS SET counter = ${wordCount.counter} where id = ${wordCount.id.get}"
      )

    } else {

      // PostgreSQLConnection.executeInsert
    }


  }

  @scala.annotation.tailrec
  def getResult(resultSet: ResultSet, list: List[WordCount] = Nil): List[WordCount] = {
    if (resultSet.next()) {
      val value = resultSet.getObject[WordCount](0, classOf[WordCount])
      getResult(resultSet, value :: list)
    }
    else {
      list
    }
  }
}
