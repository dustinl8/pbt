package com.pbt.io

import akka.util.Timeout
import com.pbt.io.ActorSystemRoot.actorSystem
import org.scalatest.{FunSpec, Matchers}

import language.postfixOps
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps
import akka.pattern.ask

/**
  * Created by gcrowell on 2017-06-16.
  */
class DownloaderTest extends FunSpec with Matchers {

  describe("Download single stock data") {
    it("sould work") {

      val request = PriceDownloadRequest("MSFT")

      val actorRef = ActorSystemRoot.forwardRequest(request)

      implicit val timeout = Timeout(5 seconds)
      val future = actorRef ? "whats taking to long"
      val result = Await.result(future, timeout.duration).asInstanceOf[String]
      println(result)

//      // import ExecutionContextExecutor
//      import ActorSystemRoot.actorSystem.dispatcher
//      // start actor system (no idea what scheduler is or does)
//      Thread.sleep(1000)
//      ActorSystemRoot.actorSystem.scheduler.scheduleOnce(100 millis) {
//        // send message to actor in the system
//        actorRef ! "test"
//        actorRef ! "test"
//      }
      Thread.sleep(3000)

    }
  }
}
