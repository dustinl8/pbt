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

case class Downloader(val request: DataDownloadRequest) extends Actor with ActorLogging {

  import akka.pattern.pipe
  import context.dispatcher

  val httpRequest = HttpRequest(uri = request.toUrlString)
  val http = Http(context.system)

  final implicit val materializer: ActorMaterializer = ActorMaterializer(ActorMaterializerSettings(context.system))
  private var responceData: String = ""

  override def preStart() = {
    log.info("preStart")
    http.singleRequest(httpRequest).pipeTo(self)
  }

  def receive = {
    case HttpResponse(StatusCodes.OK, headers, entity, _) =>
      entity.dataBytes.runFold(ByteString(""))(_ ++ _).foreach { body =>
        responceData = body.utf8String
        log.info("Got response, body")
      }
    case resp@HttpResponse(code, _, _, _) =>
      log.info("Request failed, response code: " + code)
      resp.discardEntityBytes()
    case message: String => sender() ! responceData
    case _ => println(s"${request.toUrlString} received a message.")
  }

}

object Downloader {
  def buildDownloadRequest(request: DataDownloadRequest): Props = Props(new Downloader(request))
}

object ActorSystemRoot {
  // application entry point
  // create a new actor system
  val actorSystem = ActorSystem("actor_system")
  // create parameter for Actor

  def forwardRequest(request: DataDownloadRequest): ActorRef = {
    // create Props configuration needed to safely start an actor
    val actorProps = Downloader.buildDownloadRequest(request)
    // create (and start) the actor
    actorSystem.actorOf(actorProps, "an_actor_created_using_value_class")
  }
}