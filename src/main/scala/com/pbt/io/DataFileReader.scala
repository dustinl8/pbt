package com.pbt.io

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}

import scala.io.Source

/**
  * Created by gcrowell on 2017-06-18.
  */
class DataFileReader extends Actor with ActorLogging {
  override def receive: Receive = {
    case path: String => {
      log.info("file path received.  parsing...")
      val file = Source.fromFile(path)
      file.getLines.foreach((line: String) => println(line))
    }
    case _ =>
  }
}


object DemoFileReader extends App {

  override def main(args: Array[String]): Unit = {
    class Testing123 extends Actor with ActorLogging {

      private val rdrRef = context.actorOf(Props[DataFileReader], name = "file_reader")

      override def receive: Receive = {
        case _ => {
          log.info("beginning test")
          rdrRef ! "/Users/gcrowell/Documents/git/pbt/src/main/scala/com/pbt/stocks.csv"
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


