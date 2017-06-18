package com.pbt.io

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import org.scalatest.{FunSpec, Matchers}

/**
  * Created by gcrowell on 2017-06-17.
  */
class MasterTest extends FunSpec with Matchers {
  describe("Master actor") {
    it("sanity check") {


      class Testing123 extends Actor with ActorLogging {

        private val masterRef = context.actorOf(Props[Master], name = "master_consumer")

        override def receive: Receive = {
          case x: String => println("blah blah blah")
          case _ => {
            log.info("beginning test")
            masterRef ! new PriceDownloadRequest("MSFT")
            masterRef ! new PriceDownloadRequest("DATA")
            masterRef ! new PriceDownloadRequest("APPL")
            masterRef ! new PriceDownloadRequest("FB")
            masterRef ! new PriceDownloadRequest("GS")
            masterRef ! new PriceDownloadRequest("NKE")
          }
        }
      }
      val actorSystem = ActorSystem("actor_system")

      val testingRef = actorSystem.actorOf(Props[Testing123], "consuming_system")
      testingRef ! "request test"


      println("continue on doing other work while data is downloaded/parsed")
    }
  }
}
