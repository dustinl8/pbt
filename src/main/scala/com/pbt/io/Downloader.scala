package com.pbt.io

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props, Terminated}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.HttpRequest
import akka.routing.{ActorRefRoutee, RoundRobinRoutingLogic, Router}
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

/**
  * Root/Master
  * doesn't do anything.  just receives and forwards/routes DataDownloadRequest's to 1 of it's slaves.
  */
class Master extends Actor with ActorLogging {

  val slavePoolSize = 5

  var router = {
    // create slave pool
    val slaves = Vector.fill(slavePoolSize) {
      val r = context.actorOf(Props[Downloader])
      context watch r
      ActorRefRoutee(r)
    }
    Router(RoundRobinRoutingLogic(), slaves)
  }

  def receive = {
    case w: DataDownloadRequest =>
      // forward/route work to slave pool
      router.route(w, sender())
    case Terminated(a) =>
      // replace dead slaves
      router = router.removeRoutee(a)
      val r = context.actorOf(Props[Downloader])
      context watch r
      router = router.addRoutee(r)
  }
}

/**
  * Many Downloader instances are started by Master
  *
  * receives DataDownloadRequest's from Master.
  * downloads (ie. executes single HttpRequest) the request
  * forwards downloaded data (eg. raw csv string data) to it's (only) child (ie. Parser)
  * TODO accept structured data from Parser
  */
class Downloader extends Actor with ActorLogging {

  import akka.pattern.pipe
  import context.dispatcher

  final implicit val materializer: ActorMaterializer = ActorMaterializer(ActorMaterializerSettings(context.system))

  // create 1 Parser instance (aka "slave")
  val child = context.actorOf(Props[Parser], name = "parser")

  def receive = {
    case request: DataDownloadRequest => {
      log.info(s"${request.getClass} receieved")
      val httpRequest = HttpRequest(uri = request.toUrlString)
      val http = Http(context.system)
      http.singleRequest(httpRequest).pipeTo(child)
    }
    case _ => log.info(s"unhandled message received")
  }
}


/**
  * 1 Parser instance is started per Downloader instance
  *
  * receives HttpResponse from Downloader
  * TODO parse and send structured data back to Downloader
  */
class Parser extends Actor with ActorLogging {

  import context.dispatcher

  final implicit val materializer: ActorMaterializer = ActorMaterializer(ActorMaterializerSettings(context.system))

  override def receive: Receive = {
    case HttpResponse(StatusCodes.OK, headers, entity, _) =>
      entity.dataBytes.runFold(ByteString(""))(_ ++ _).foreach { body =>
        log.info("http response received")
        parseCsvData(body.utf8String)
      }
    case resp@HttpResponse(code, _, _, _) =>
      // TODO handle StatusCodes != OK
      log.info("Request failed, response code: " + code)
      resp.discardEntityBytes()
    case _ => log.info("unhandled message received")
  }

  //TODO parse data
  def parseCsvData(csv: String): Unit = {
    log.info(s"parsing...")
    //    log.info(csv)

  }
}


object Main {

  def main(args: Array[String]): Unit = {
    val actorSystem = ActorSystem("actor_system")
    val masterRef = actorSystem.actorOf(Props[Downloader], "master_actor")
    masterRef ! new PriceDownloadRequest("MSFT")
    masterRef ! new PriceDownloadRequest("DATA")
    masterRef ! new PriceDownloadRequest("APPL")
    masterRef ! new PriceDownloadRequest("FB")
    masterRef ! new PriceDownloadRequest("GS")
    masterRef ! new PriceDownloadRequest("NKE")

    println("continue on doing other work while data is downloaded/parsed")
  }
}
