package fr.christophe

import fr.christophe.twitter.client.StreamClient

/**
 * Hello world!
 *
 */
object Main extends App {
  println( "Begin!" )

  new StreamClient().keepAliveConnetion()

  println( "End!" )

}
