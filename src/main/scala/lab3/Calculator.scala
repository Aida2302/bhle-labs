package lab3

object Calculator extends App {
  sealed trait Calculator

  case class Success(result: Int) extends Calculator

  case class Fail(message: String) extends Calculator




  case class Water(size: Int, source: Source, carbonated: Boolean)

  sealed trait Source

  case object Well extends Source
  case object Spring extends Source
  case object Tap extends Source

}
