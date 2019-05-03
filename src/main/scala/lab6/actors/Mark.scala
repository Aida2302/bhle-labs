package lab6.actors

import lab6.actors.Mark.GetData
import akka.actor.{Actor, ActorLogging, Props}
import lab6.model.MarkModel

object Mark {

  def props(value: Int) = Props(new Mark(value))

  case object GetData

}

class Mark(value: Int) extends Actor with ActorLogging{
  override def receive: Receive = {
    case GetData =>
      log.info("Received GetMark request")
      sender() ! MarkModel(value)
  }
}
