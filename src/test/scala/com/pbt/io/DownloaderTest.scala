package com.pbt.io

import akka.util.Timeout
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

      trait x
      class y extends x
      val z = new y

      z match {
        case tmp: x => println("matched on trait")
        case _ => println("no match")
      }

    }
  }
}
