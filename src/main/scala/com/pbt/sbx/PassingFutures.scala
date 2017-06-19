package com.pbt.sbx

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.pattern.ask
import akka.pattern.pipe
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import akka.util.Timeout

import scala.concurrent.{Future, Promise}
import scala.concurrent.duration._
import scala.io.Source
import scala.language.postfixOps
import scala.util.{Success, Try}


class StartSignal

/**
  * SlaveMaster creates child actor.  Then sends message to child actor and "asks" for result from child by "piping"  result to itself
  */
class SlaveMaster extends Actor with ActorLogging {
  val child = context.actorOf(Props[SlowSlave], name = "SlowSlave_actor")

  import context.dispatcher

  implicit val timeout = Timeout(5 seconds)

  // Master waits until it receives at StartSignal
  override def receive: Receive = {
    // Master handles StartSignal
    case startSignal: StartSignal => {
      log.info("received message to begin")
      // Master starts child actor with ? operator ("ask" operator)
      // child triggered to do long running operation so result is not immediately available
      // create place holder for result (Future[Int])
      val charCount: Future[Int] = (child ? startSignal).mapTo[Int]
      log.info("message sent to child actor")
      // instead of waiting for result (ie blocking)
      // just tell place holder to send it to SlaveMaster whenever result arrives
      charCount pipeTo self
      log.info("don't wait for result from child")
      // execution flow continues...
    }
    case charCount: Int => {
      log.info(s"result from child received: charCount = $charCount")
    }
    case _ => log.info("unknown message received")
  }
}


class SlowSlave extends Actor with ActorLogging {

  import context.dispatcher

  final implicit val materializer: ActorMaterializer = ActorMaterializer(ActorMaterializerSettings(context.system))

  override def receive: Receive = {

    // do some slow running operation
    case startSignal: StartSignal => {
      log.info("StartSignal received")

      // create placeholder for result
      val charCount: Future[Int] = Future {
        log.info("slow running operation started")
        val foo = Source.fromFile("/Users/gcrowell/Documents/git/pbt/src/main/scala/com/pbt/stocks.csv")
        val count = foo.map((c: Char) => if (c == 'A') 1 else 0).sum
        foo.close()
        count
      }
      // don't wait for result but when it arrives forward it to sender()
      charCount.pipeTo(sender())
      log.info("execution path in child continues without delay")

    }
    case _ => log.info("unknown message received")
  }
}


object DemoPassingFutures extends App {


  override def main(args: Array[String]): Unit = {
    class Testing123 extends Actor with ActorLogging {

      private val masterRef = context.actorOf(Props[SlaveMaster], name = "master_consumer")

      override def receive: Receive = {
        case _ => {
          log.info("beginning test")
          masterRef ! new StartSignal
        }
      }
    }
    val actorSystem = ActorSystem("actor_system")

    val testingRef = actorSystem.actorOf(Props[Testing123], "consuming_system")
    testingRef ! "request test"


    println("continue on doing other work while data is downloaded/parsed")
    Thread.sleep(3000)

    println("execution complete.  stopping actor system...")
    actorSystem.terminate()
  }
}



