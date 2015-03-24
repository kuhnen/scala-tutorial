package com.github.kuhnen.actor

import akka.actor._
import akka.event.LoggingReceive
import akka.pattern.pipe
import spray.client.pipelining._
import spray.http.{Uri, _}

import scala.concurrent.Future
import scala.reflect.ClassTag
import scala.util.Try


/**
 * Created by kuhnen on 3/23/15.
 */
class Links[A <: Uri](level: Int, maxLevel: Int)(implicit ct: ClassTag[A], mf: Manifest[A]) extends Actor with ActorLogging {

  import com.github.kuhnen.actor.Links._

  implicit val ec = context.dispatcher
  val pipeline: HttpRequest => Future[HttpResponse] = sendReceive
  var children = Set.empty[ActorRef]
  var urisTotal = Set.empty[Uri]

  override def receive = {

    case uri: A =>
      val responseFuture = pipeline(Get(uri))
      val sourceFuture = responseFuture.map(_.entity.asString)
      val linksFuture: Future[Set[String]] = sourceFuture.map(source => source.getLinksWithProtocol("http").toSet)

      val urisFuture: Future[Set[Uri]] = for {
        links <- linksFuture
      } yield links.map(link => Try(Uri(link))).filter(_.isSuccess).map(_.get)

      if (level < maxLevel) {
        log.debug(s"Creating actors for level $level")
        val levelDownActors = urisFuture.map { uris =>
          var uriCounter = 0
          uris.take(3).map { uri =>
            log.debug(s"Creating actor for $uri")
            val nextLevel = level + 1
            val actor = context.actorOf(Links.props(nextLevel, maxLevel), name = s"links$nextLevel-$uriCounter")
            uriCounter += 1
            children = children + actor
            actor ! uri
          }

        }
      }
      urisFuture pipeTo self

    case uris: Set[A] =>
      children = children - sender()
      urisTotal = urisTotal ++ uris
      val total = urisTotal.size
      if (children.isEmpty) {
        log.info(s"We are done here. Total links: $total")
        context.parent ! urisTotal
        if (level == 0)
          self ! PoisonPill
      }
  }

}


object Links {

  def props[A](level: Int, maxLevel: Int)(implicit ct: ClassTag[A], mf: Manifest[A]) = Props(classOf[Links[A]], level, maxLevel, ct, mf)

  implicit class WebSourceString(source: String) {
    def getLinks: Iterator[String] = {
      val aTags = htmlahrefaTagPattern.findAllIn(source)
      val links: Iterator[String] = aTags.flatMap { atag =>
        htmlahrefaTagPattern.findAllIn(atag).matchData.map(m => m.group(1).replace("\"", ""))
      }
      links
    }

    def getLinksWithProtocol(protocol: String = "http") = getLinks.filter(_.startsWith(protocol))

  }

  val htmlaTagPattern = "(?i)<a([^>]+)>(.+?)</a>".r
  val htmlahrefaTagPattern = "\\s*(?i)href\\s*=\\s*(\"([^\"]*\")|'[^']*'|([^'\">\\s]+))".r
  //"<a.+href=\"(http://[^\"]+)\".*>".r


}
