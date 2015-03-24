package com.github.kuhnen

import java.net.URL
import java.io.InputStream
import scala.concurrent.Future
import scala.io.Source
import scala.util.Try

/**
 * Created by kuhnen on 3/23/15.
 */

object MonadsTutorial {


  def main(args: Array[String]) {

  }
}

//First example:
// parseUrl("http://www.google.com").map(_.getProtocol)
// parseUrl("notValidUrl").map(_.getProtocol)

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
      source = Source.fromInputStream(is)
    } yield source.getLines()

  //def getURLContentFuture(url: String) = Future {

  //}
  //SimilarOffers example:
  //https://github.com/chaordic/platform-market/blob/master/core/src/main/scala/com/chaordicsystems/platform/market/core/business/OfferBusiness.scala#L48-L71

}



