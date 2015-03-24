package com.github.kuhnen

import akka.actor._
import akka.event.LoggingReceive
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._

/**
 * Created by kuhnen on 24/03/15.
 */

sealed trait CoffeeRequest
case object CappuccinoRequest extends CoffeeRequest
case object EspressoRequest extends CoffeeRequest
case object WieGehts extends CoffeeRequest
case class Bill(cents: Int)
case object ClosingTime
case object CaffeineWithdrawalWarning

object Barista {

  def props() = Props[Barista]
}

class Customer(caffeineSource: ActorRef) extends Actor {

  override def preStart() {
    context.watch(caffeineSource)
  }

  def receive = {
    case CaffeineWithdrawalWarning => caffeineSource ! EspressoRequest
    case Bill(cents) => println(s"I have to pay $cents cents, or else!")
    case Terminated(actorRef) => println(s"Ohhh my caffeine source died -> $actorRef ")
  }
}

object Customer {

  def props(caffeineSource: ActorRef) = Props(classOf[Customer], caffeineSource)
}


class Barista extends Actor {

  var cappuccinoCount = 0
  var espressoCount = 0

  def receive = LoggingReceive {
    case CappuccinoRequest =>
      cappuccinoCount += 1
      sender ! Bill(250)
      println("I have to prepare a cappuccino!")
    case EspressoRequest =>
      espressoCount += 1
      sender ! Bill(200)
      println("Let's prepare an espresso.")

    case WieGehts=> sender ! s"Sehr Gut, danke. I sold $espressoCount espressos and $cappuccinoCount cappuccino"

    case ClosingTime => context.system.shutdown()

  }
}

object BaristaExample extends App with ShutDownSystemHook {

  implicit val timeout = Timeout(1 second)

  override val system: ActorSystem = ActorSystem("CoffeHouse")
  implicit val ec = system.dispatcher

  val barista: ActorRef = system.actorOf(Barista.props(), "Barista")
  val customer = system.actorOf(Customer.props(barista), "Customer")
  customer ! CaffeineWithdrawalWarning
  barista ! CappuccinoRequest
  barista ! EspressoRequest
  println("I ordered a cappuccino and an espresso")
  println("How is he doing?")
  val response = barista ? WieGehts
  response.foreach { case answer =>  println(s"The barista is $answer")  }

  Thread.sleep(2000)
  barista ! PoisonPill
  Thread.sleep(2000)
  sys.exit()
  //barista ! ClosingTime





}
