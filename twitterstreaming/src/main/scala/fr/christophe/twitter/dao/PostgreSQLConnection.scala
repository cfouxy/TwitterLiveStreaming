package fr.christophe.twitter.dao

import java.sql._

object PostgreSQLConnection {
  // tweets_wordCounters_word_idx

  classOf[org.postgresql.Driver]
  private val server = "localhost"
  private val port = "5432"
  private val dbName = "tweeter"
  private val user = "postgres"
  private val password = "postgres"

  private val con_str = s"jdbc:postgresql://$server:$port/$server?user=$user&password=$password"


  def executeSelect(query: String): ResultSet = {
    var conn: Option[Connection] = None
    var stmt: Option[Statement] = None
    try {
      conn = Some(DriverManager.getConnection(con_str))
      stmt = Some(conn.get.createStatement())
      stmt.get.executeQuery(query)
    } finally {
      if (stmt.isDefined) stmt.get.close
      if (conn.isDefined) conn.get.close
    }
  }

  def executeUpdate(query: String): Int = {
    var conn: Option[Connection] = None
    var stmt: Option[Statement] = None
    try {
      conn = Some(DriverManager.getConnection(con_str))
      stmt = Some(conn.get.createStatement())
      stmt.get.executeUpdate(query)
    } finally {
      if (stmt.isDefined) stmt.get.close
      if (conn.isDefined) conn.get.close
    }
  }


  def executeInsert(query: String, wordCount: WordCount): Boolean = {
    var conn: Option[Connection] = None
    var stmt: Option[PreparedStatement] = None
    try {
      conn = Some(DriverManager.getConnection(con_str))
      stmt = Some(conn.get.prepareStatement(query))
      stmt.get.setString(1, wordCount.word)
      stmt.get.setInt(2, wordCount.counter)
      stmt.get.execute()
    } finally {
      if (stmt.isDefined) stmt.get.close
      if (conn.isDefined) conn.get.close
    }
  }

}
