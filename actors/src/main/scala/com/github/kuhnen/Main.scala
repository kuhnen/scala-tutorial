package com.github.kuhnen

import akka.actor.ActorSystem
import com.github.kuhnen.actor.Links
import spray.http.Uri

/**
 * Created by kuhnen on 3/23/15.
 */

trait ShutDownSystemHook {

  val system: ActorSystem
  sys.addShutdownHook {
    system.shutdown()
    system.awaitTermination()
  }

}

object Main extends App {

  val system: ActorSystem = ActorSystem("Crawler")

  val links = system.actorOf(Links.props[Uri](0, 3, 10), name = "links0")
  val google = Uri("http://www.centauro.com.br")
  links ! google

  sys.addShutdownHook {
    system.shutdown()
    system.awaitTermination()
  }

}

object SystemExample extends App with ShutDownSystemHook {

  override val system = ActorSystem("Example")
  Thread.sleep(1000)
  sys.exit()
}