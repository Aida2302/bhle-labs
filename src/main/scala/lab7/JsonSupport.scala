package lab7

import spray.json.RootJsonFormat
import spray.json.DefaultJsonProtocol._
import lab7.models._

trait JsonSupport {
  implicit val heroFormat: RootJsonFormat[Student] = jsonFormat3(Student)
  implicit val heroesFormat: RootJsonFormat[Students] = jsonFormat1(Students)
  implicit val responseFormat: RootJsonFormat[Response] = jsonFormat1(Response)
}
