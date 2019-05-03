package lab7

import akka.actor.{Actor, Props}
import slick.jdbc.MySQLProfile
import slick.jdbc.MySQLProfile.api._
import lab7.models.{ Student, Students}

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

object Teacher {

  case class AddStudent(name: String, surname: String)

  case object GetStudents

  def props(db: MySQLProfile.backend.Database) = Props(new Teacher(db))
}

class Teacher(db: MySQLProfile.backend.Database) extends Actor {

  import Teacher._

  val studentsTable = TableQuery[StudentsTable]

  val students: Seq[Student] = Await.result(db.run(studentsTable.result), 5.seconds)

  override def receive: Receive = {
    case AddStudent(newName, newSurname) =>
    sender() ! db.run(
      studentsTable += Student(name = newName, surname = newSurname)
    )


    case GetStudents =>
      sender() ! Students(students)

  }
}
