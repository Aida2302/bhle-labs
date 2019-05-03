package lab6

import lab6.actors.{Student, Teacher}
import lab6.model.{MarkModel, MarkModelPostPut, StudentModelPostPut}
import spray.json.DefaultJsonProtocol.{jsonFormat1, jsonFormat2, jsonFormat3, jsonFormat4}
import spray.json.RootJsonFormat
import spray.json.DefaultJsonProtocol._

trait JsonSupport {
  implicit val teacherResponseFormat: RootJsonFormat[Teacher.Response] = jsonFormat1(Teacher.Response)
  implicit val studentModelFormat: RootJsonFormat[model.StudentModel] = jsonFormat4(model.StudentModel)
  implicit val teacherStudentsFormat = jsonFormat1(Teacher.Students)
  implicit val studentMarksFormat = jsonFormat1(Student.Marks)
  implicit val studentModelPostPutFormat: RootJsonFormat[StudentModelPostPut] = jsonFormat3(StudentModelPostPut)
  implicit val teacherStatusInfoFormat: RootJsonFormat[Teacher.StatusInfo] = jsonFormat2(Teacher.StatusInfo)
  implicit val markModelFormat: RootJsonFormat[MarkModel] = jsonFormat1(MarkModel)
  implicit val markModelPostPutFormat: RootJsonFormat[MarkModelPostPut] = jsonFormat2(MarkModelPostPut)
}
