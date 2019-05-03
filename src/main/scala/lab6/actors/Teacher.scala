package lab6.actors

import lab6.actors.Teacher._
import akka.actor.{Actor, ActorLogging, ActorRef, Props, ReceiveTimeout}
import lab6.model.{MarkModel, StudentModel}
import lab6.utils.StatusCodes

import scala.concurrent.duration._

object Teacher {

  case class AddStudent(id: Int, name: String, surname: String, birthYear: Int)

  case class DeleteStudent(id: Int)

  case class Students(students: Seq[StudentModel])

  case object GetStudents

  case class EditStudent(id: Int, name: String, surname: String, birthYear: Int)

  case class Response(message: String)

  case class PutMark(id: Int, markId: Int, value: Int)

  case class GetStudent(id: Int)

  case class GetStudentByName(name: String)

  case class GetMarks(id: Int)

  case class Marks(marks: Seq[MarkModel])

  case class StatusInfo(statusCode: Int, message: String)

  def props() = Props(new Teacher())


}

class Teacher extends Actor with ActorLogging{

  context.setReceiveTimeout(5.seconds)

  var students = Map.empty[Int, ActorRef]
  var marks = Map.empty[Int, ActorRef]

  override def receive: Receive = {
    case AddStudent(id, name, surname, birthYear) =>
      log.info(s"Student with name $name added")
      val student: ActorRef = context.actorOf(Student.props(id, name, surname, birthYear))
      students = students + (id -> student)
      sender() ! Teacher.Response("OK")

    case DeleteStudent(id) =>
      log.info(s"Student with id $id deleted")
      //sender() ! Teacher.Response("OK")
      students = students - id
      sender() ! Teacher.Response("OK")

    case Teacher.GetStudents =>
      log.info("Received GetStudents")
      students.values.foreach(studentActor => studentActor ! Student.GetData)
      context.become(waitingResponses(students.size, sender(), Seq.empty[StudentModel]))

    case PutMark(id, markId, value) =>
      log.info(s"Student with id $id get $value")
      students.get(id) match {
        case Some(studentRef) =>
          studentRef ! Student.AddMark(markId, value)
          context.become(waitingAck(sender()))
        case None =>
          log.error(s"Student with id: $id is not specified")
          sender() ! Left(StatusInfo(StatusCodes.NOT_FOUND, s"Account with accountId: $id is not specified"))
      }

    case EditStudent(id, name, surname, birthYear) =>
      log.info(s"Received edit student")
      students.get(id) match{
        case Some(studentRef) =>
          students -= id
          studentRef ! Student.SetData(name, surname, birthYear)
          val student: ActorRef = context.actorOf(Student.props(id, name, surname, birthYear))
          students += (id -> student)
          context.become(waitingStudentResponse(sender()))
        case None =>
          log.error(s"Student with id: $id is not specified")
          sender() ! Left(StatusInfo(StatusCodes.NOT_FOUND, s"Student with id: $id is not found"))
      }

    case Teacher.GetStudent(id) =>
      log.info(s"Received GetStudent with id: $id")
      students.get(id) match {
        case Some(studentModel) =>
          studentModel ! Student.GetData
          context.become(waitingStudentResponse(sender()))
        case None =>
          log.error(s"Student with id: $id is not specified")
          sender() ! Left(StatusInfo(StatusCodes.NOT_FOUND, s"Student with clientId: $id is not found"))
      }



    case GetMarks(id) =>
      log.info(s"Received GetMarks request of student $id")
      students.get(id) match{
        case Some(studentRef) =>
          studentRef ! Student.GetMarks
          //context.become(waitingMarkResponses(marks.size, sender(), Seq.empty[MarkModel]))
          //context.become(waitingStudentResponse(sender()))
          sender() ! Right(StatusInfo(StatusCodes.SUCCESS, Student.Marks.toString))

        case None =>
          log.error(s"Student with id: $id is not specified")
          sender() ! Left(StatusInfo(StatusCodes.NOT_FOUND, s"Student with clientId: $id is not found"))
      }


    case GetStudentByName(name) =>
      log.info(s"Received GetStudent with $name")


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


  def waitingStudentResponse(replyTo: ActorRef): Receive = {
    case student: StudentModel =>
        replyTo ! student
        context.become(receive)

    case mark: MarkModel =>
      replyTo ! Right(mark)
      context.become(receive)

    case ReceiveTimeout =>
      log.error("Received timeout while waiting for Response")
      replyTo ! Left(StatusInfo(StatusCodes.REQUEST_TIMEOUT, "Received timeout exception"))
      context.become(receive)
  }

  def waitingResponses(responsesLeft: Int, replyTo: ActorRef, students: Seq[StudentModel]): Receive = {
    case student: StudentModel =>
      log.info(s"Received StudentModel with name: ${student.name}. Responses left: $responsesLeft")
      if (responsesLeft - 1 == 0) {
        log.info("All responses received, replying to initial request.")
        replyTo ! Teacher.Students(students :+ student)
        context.become(receive)
      }
      else context.become(waitingResponses(responsesLeft - 1, replyTo, students = students :+ student))
  }


  def waitingAck(replyTo: ActorRef): Receive = {
    case Student.Acknowledge(requestId, message) =>
      replyTo ! Right(StatusInfo(StatusCodes.SUCCESS, message))
      context.become(receive)

    case Student.NoAcknowledge(requestId, message) =>
      replyTo ! Right(StatusInfo(StatusCodes.BAD_REQUEST, message))
      context.become(receive)

    case ReceiveTimeout =>
      log.error("Received timeout while waiting for Ack(s)")
      replyTo ! Left(StatusInfo(StatusCodes.REQUEST_TIMEOUT, "Request time out"))
      context.become(receive)
  }
}
