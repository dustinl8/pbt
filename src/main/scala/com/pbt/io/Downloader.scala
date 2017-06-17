package com.pbt.io

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.HttpRequest
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import akka.util.ByteString

import language.postfixOps
import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * Created by gcrowell on 2017-06-16.
  */

trait DataDownloadRequest {
  def toUrlString: String
}
abstract class DataDownloadRequest2 extends DataDownloadRequest{
}


class Downloader extends Actor with ActorLogging {

  import akka.pattern.pipe
  import context.dispatcher

  final implicit val materializer: ActorMaterializer = ActorMaterializer(ActorMaterializerSettings(context.system))

  val child = context.actorOf(Props[Parser], name = "parser")

  def receive = {
    case request : DataDownloadRequest2 => {
      log.info(s"${request.getClass} receieved")
      val httpRequest = HttpRequest(uri = request.toUrlString)
      val http = Http(context.system)
      http.singleRequest(httpRequest).pipeTo(child)
    }
    case _ => log.info(s"unhandled message received")
  }
}

class Parser extends Actor with ActorLogging {

  import context.dispatcher

  //TODO parse data
  def parseCsvData(csv: String): Unit = {
    log.info(s"parsing\n$csv")
  }

  final implicit val materializer: ActorMaterializer = ActorMaterializer(ActorMaterializerSettings(context.system))

  override def receive: Receive = {
    case HttpResponse(StatusCodes.OK, headers, entity, _) =>
      entity.dataBytes.runFold(ByteString(""))(_ ++ _).foreach { body =>
        log.info("http response received")
        parseCsvData(body.utf8String)
      }
    case resp@HttpResponse(code, _, _, _) =>
      log.info("Request failed, response code: " + code)
      resp.discardEntityBytes()
    case _ => log.info("unhandled message received")
  }
}

object Main {

  def main(args: Array[String]): Unit = {
    val actorSystem = ActorSystem("actor_system")
    val masterRef = actorSystem.actorOf(Props[Downloader], "master_actor")
    masterRef ! new PriceDownloadRequest("MSFT")
  }
}
