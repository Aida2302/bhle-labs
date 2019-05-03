package lab6

import akka.http.scaladsl.server.Directives.{as, complete, concat, delete, entity, get, parameter, parameters, path, pathPrefix, post, put}
import lab6.actors.Teacher
import lab6.model.{MarkModelPostPut, StudentModel, StudentModelPostPut}
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives.{concat, pathPrefix}
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.Directives._
import org.slf4j.LoggerFactory
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._


object Boot extends App with JsonSupport {

  val log = LoggerFactory.getLogger("Boot")

  // needed to run the route
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  // needed for the future map/flatmap in the end and future in fetchItem and saveOrder
  implicit val executionContext = system.dispatcher

  //timeout for FUTURE
  implicit val timeout = Timeout(30.seconds)

  val teacher = system.actorOf(Teacher.props(), "teacher")

  val route =
    pathPrefix("teacher") {
      concat(
        path("student") {
          concat(
            post {
              entity(as[StudentModel]) { studentModel =>
                complete {
              //"OK"
                (teacher ? Teacher.AddStudent(studentModel.id, studentModel.name, studentModel.surname, studentModel.birthYear)).mapTo[Teacher.Response]
              }
            }
          },
            get {
              parameters("id".as[Int]) { id =>
                complete {
                  (teacher ? Teacher.GetStudent(id)).mapTo[StudentModel]
                }
              }
            },
            delete {
              parameter("id".as[Int]) { id =>
                complete {
                  (teacher ? Teacher.DeleteStudent(id)).mapTo[Teacher.Response]
                }
              }
            }
          )
        },
        path("students") {
          get {
            complete {
              (teacher ? Teacher.GetStudents).mapTo[Teacher.Students]
            }
          }
        },
        pathPrefix("student" / IntNumber) { id =>
          put {
            entity(as[StudentModelPostPut]) { studentModel =>
              complete {
                (teacher ? Teacher.EditStudent(id, studentModel.name, studentModel.surname, studentModel.birthYear))
                  .mapTo[StudentModel]
              }
            }
          } ~
          pathPrefix("marks") {
            path("mark"){
              concat(
                get{
                  complete {
                    (teacher ? Teacher.GetMarks(id)).mapTo[Either[Teacher.StatusInfo, Teacher.StatusInfo]]
                  }
                },
                post {
                  entity(as[MarkModelPostPut]){ markModel =>
                    complete{
                      (teacher ? Teacher.PutMark(id, markModel.markId, markModel.value)).mapTo[Either[Teacher.StatusInfo, Teacher.StatusInfo]]
                    }
                  }
                }
              )
            }
          }
        }
      )
    }


  val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)
  log.info("Listening on port 8080...")


}
