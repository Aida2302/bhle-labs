package lab6.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import lab6.model.{MarkModel, StudentModel}

object Student {

  def props(id: Int, name: String, surname: String, birthYear: Int) = Props(new Student(id, name, surname, birthYear))

  case object GetData

  case class SetData(name: String, surname: String, birthYear: Int)

  case class Marks(marks: Seq[MarkModel])

  case class AddMark(id: Int, value: Int)

  case object GetMarks

  case class Acknowledge(id: Int, message: String)

  case class NoAcknowledge(id: Int, message: String)

}

class Student(id: Int, name: String, surname: String, birthYear: Int) extends Actor with ActorLogging {
  import Student._

  var marks = Map.empty[Int, ActorRef]
  var state: StudentModel = StudentModel(id, "", "", 0)
  var stateMark: MarkModel = MarkModel(0)

  override def receive: Receive = {
    case GetData =>
      log.info("Received GetData request")
      sender() ! StudentModel(id, name, surname, birthYear)

    case AddMark(id, value) =>
      log.info(s"Received AddMark")
      val mark: ActorRef = context.actorOf(Mark.props(value))
      marks += id -> mark
      sender ! Acknowledge(id, "Added mark")

    case GetMarks =>
      log.info("Received GetMarks")
      marks.values.foreach(markActor => markActor ! Mark.GetData)
      //sender() ! stateMark
      context.become(waitingMarkResponses(marks.size, sender(), Seq.empty[MarkModel]))

    case SetData(name, surname, birthYear) =>
      log.info("Received SetData request")
      state = StudentModel(state.id, name, surname, birthYear)
      sender() ! state
  }

  def waitingMarkResponses(responsesLeft: Int, replyTo: ActorRef, marks: Seq[MarkModel]): Receive = {
    case mark: MarkModel =>
      log.info(s"Received MarkModel with value: ${mark.value}. Responses left: $responsesLeft")
      if (responsesLeft - 1 == 0) {
        log.info("All responses received, replying to initial request.")
        replyTo ! Teacher.Marks(marks :+ mark)
        context.become(receive)
      }
      else context.become(waitingMarkResponses(responsesLeft - 1, replyTo, marks = marks :+ mark))
  }

}
