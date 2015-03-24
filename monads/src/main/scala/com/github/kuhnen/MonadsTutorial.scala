package com.github.kuhnen

import java.net.{MalformedURLException, URL}
import java.io.InputStream
import scala.concurrent.Future
import scala.io.{Codec, Source}
import scala.util.Try

/**
 * Created by kuhnen on 3/23/15.
 */

object MonadsTutorialTryExample {

  import UrlExample._

  def main(args: Array[String]): Unit = {

    val maybeProtocol: Try[String] = parseURL("http://www.google.com").map(_.getProtocol)

    maybeProtocol.foreach { protocol =>
      println(s"The protocol is $protocol" )
    }

    val maybeNotProtocol = parseURL("notValidUrl").map(_.getProtocol)

    maybeNotProtocol.foreach { protocol =>
      println(s"The protocol is $protocol" )
    }

    val maybeNotProtocolWithRecover: Try[String] = parseURL("notValidUrl").map(_.getProtocol).recover { case e: Exception =>
      s"OOHHHH that was a bad URL was bad his URL: ${e}"
    }


    maybeNotProtocolWithRecover.foreach { protocol =>
      println(s"The protocol is $protocol" )
    }

    getURLContent("http://www.google.com").foreach(it => it.take(1) foreach println)

    val math = Try(1)
    val fuckedOperation = math.map(_ + 4).map(_ / 0).map(_ * 10)
    println(fuckedOperation)
    val fuckedOperationWithRecover = math.map(_ + 4).map(_ / 0).map(_ * 10).recover { case e: Exception => 0}
    println(fuckedOperationWithRecover)

  }


}


object UrlExample {

  def parseURL(url: String): Try[URL] = Try(new URL(url))

  def inputStreamForURLWrong(url: String): Try[Try[Try[InputStream]]] = parseURL(url).map { u =>
    Try(u.openConnection()).map(conn => Try(conn.getInputStream))
  }

  def inputStreamForURL(url: String): Try[InputStream] = {
    parseURL(url)
      .flatMap { u => Try(u.openConnection()).flatMap(conn => Try(conn.getInputStream))
    }
  }

  def inputStreamURLForComprehensions(url: String): Try[InputStream] = for {
    url <- parseURL(url)
    connection <- Try(url.openConnection())
    stream <- Try(connection.getInputStream)
  } yield stream


  def getURLContent(url: String): Try[Iterator[String]] =
    for {
      url <- parseURL(url)
      connection <- Try(url.openConnection())
      is <- Try(connection.getInputStream)
      source = Source.fromInputStream(is)(Codec.ISO8859)
    } yield source.getLines()


}



