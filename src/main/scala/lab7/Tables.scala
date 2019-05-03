package lab7

import slick.jdbc.MySQLProfile.api._
import slick.lifted.{ProvenShape, Tag}
import lab7.models.Student

class StudentsTable(tag: Tag) extends Table[Student](tag, "STUDENTS") {
  // This is the primary key column:
  def studentId: Rep[Int] = column[Int]("STUDENT_ID", O.PrimaryKey, O.AutoInc)
  def studentName: Rep[String] = column[String]("STUDENT_NAME")
  def studentSurname: Rep[String] = column[String]("STUDENT_SURNAME")

  def * : ProvenShape[Student] = (studentId.?, studentName, studentSurname) <> (Student.tupled, Student.unapply)
}
