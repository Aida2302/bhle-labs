package lab6

import akka.actor.{Actor, ActorLogging, Props}
import lab6.actors.Teacher

object Bot {
  def props() = Props(new Bot())

  case object StartTest

}

class Bot extends Actor with ActorLogging{
  import Bot._

  val teacher = context.actorOf(Teacher.props(), "teacher")

  override def receive: Receive = {
    case StartTest =>
      teacher ! Teacher.AddStudent(1, "Aida", "Ualibekova", 1999)
      teacher ! Teacher.AddStudent(2, "Zhanel", "Turlybayeva", 1998)
      teacher ! Teacher.AddStudent(3, "Aisultan", "Kali", 1998)
      teacher ! Teacher.AddStudent(4, "Bob", "Marley", 1945)
      teacher ! Teacher.AddStudent(5, "Nazerke", "Kengessova", 1999)

      teacher ! Teacher.GetStudents

    case Teacher.Students(students) =>
      students.foreach(a => log.info(s"${a.name}"))
  }
}
