import java.util.Calendar
import java.text.SimpleDateFormat

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.HttpRequest
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import akka.util.ByteString
import akka.pattern.ask
import language.postfixOps
import scala.concurrent.duration._
import scala.language.postfixOps


class ToDo(val spec: String) {
  override def toString: String = spec
}

class Done(val workDone: String) {
  override def toString: String = workDone
}


class Master extends Actor with ActorLogging {
  override val log = Logging(context.system, this)
  val child = context.actorOf(Props[Slave], name = "myChild")

  def receive = {
    case "test" => log.info("master received test")
    case done: Done => log.info(s"master received done message from ${sender()}")
    case todo: ToDo => {
      log.info(s"master recieved todo message.  forwarding to: ${child}")
      // http://doc.akka.io/docs/akka/current/scala/actors.html#forward-message
      child forward todo
    }
    case _ => log.info("master received unknown message")
  }
}


class Slave extends Actor with ActorLogging {

  override val log = Logging(context.system, this)

  def receive = {
    case "test" => log.info("slave received test")
    case todo: ToDo => log.info(s"slave received todo message: ${todo.toString}")
    case _ => log.info("slave received unknown message")
  }
}

object Main {

  def start(): Unit = {
    val actorSystem = ActorSystem("actor_system")
    actorSystem.actorOf(Props[Master], "an_actor_created_using_value_class")
  }

}


Main.start()