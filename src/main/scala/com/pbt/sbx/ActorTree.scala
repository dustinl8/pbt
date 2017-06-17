package com.pbt.sbx

/**
  * Created by gcrowell on 2017-06-17.
  */

import akka.actor.{Actor, ActorLogging, ActorSystem, Kill, PoisonPill, Props}
import akka.event.Logging

import scala.concurrent.Future
import scala.language.postfixOps


/**
  * repersents a message sent between Actors.  Master sends Todo to a child actor.
  *
  * @param spec
  */
class ToDo(val spec: String) {
  override def toString: String = spec
}

/**
  * repersents a message sent between Actors.  Slave sends Done to Master.
  *
  * @param workDone
  */
class Done(val workDone: String) {
  override def toString: String = workDone
}


class Master extends Actor with ActorLogging {

  override val log = Logging(context.system, this)
  val child = context.actorOf(Props[Slave], name = "myChild")


  def receive = {
    case "fuck you" => {
      log.info(s"master recieved an insult")
      sender() ! "fuck me?"
      sender() ! "no"
      sender() ! "fuck you"
    }
    case done: Done => log.info(s"master received done message\n\t${done.toString}\n\t from ${sender()}")
    case todo: ToDo => {
      log.info(s"master recieved todo message.  forwarding to: ${child}")
      child ! todo
    }
    case message: String => log.info(s"master received unknown String message: ${message}")
    case future: Future[String] => {
      log.info(s"master recieved future: ${future.mapTo[String]}")
    }
    case _ => log.info("master received unknown message")
  }
}


class Slave extends Actor with ActorLogging {

  import context.dispatcher

  override val log = Logging(context.system, this)

  def receive = {
    case todo: ToDo => {
      log.info(s"slave received todo message: ${todo.toString}")
      sender() ! "fuck you"

    }
    case "fuck you" => {
      sender() ! "i quit"
      log.info(s"slave is murdering his master")
      sender() ! PoisonPill
      sender() ! "take that!"
      log.info(s"now only slave is alive")

    }
    case message: String => log.info(s"slave received String message: ${message}")
    case _ => log.info("slave received unknown message type")
  }
}

object Main {

  def main(args: Array[String]): Unit = {
    val actorSystem = ActorSystem("actor_system")
    val masterRef = actorSystem.actorOf(Props[Master], "master_actor")
    masterRef ! new ToDo("get me my lunch")
  }
}


