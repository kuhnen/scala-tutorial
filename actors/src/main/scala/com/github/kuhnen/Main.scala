package com.github.kuhnen

import akka.actor.ActorSystem
import com.github.kuhnen.actor.Links
import spray.http.Uri

/**
 * Created by kuhnen on 3/23/15.
 */
object Main extends App {

  val system: ActorSystem = ActorSystem("Crawler")

  val links = system.actorOf(Links.props[Uri](0, 5), name = "links0")
  val google = Uri("http://www.guiafloripa.com.br")
  links ! google

  sys.addShutdownHook {
    system.shutdown()
    system.awaitTermination()
  }

}
