package lab3

object Boot extends App {
  sealed trait Shape {
    def sides: Int

    def perimeter(): Double

    def area(): Double
  }

  trait Rectangular extends Shape {
    def height: Double

    def width: Double

    override def sides = 4

    override def perimeter() = 2.0 * (height + width)

    override def area() = height * width

  }


  case class Circle(radius: Int) extends Shape {
    def r: Double = radius

    override def sides = 0

    override def perimeter() = 2*radius*math.Pi

    override def area() = radius*radius*math.Pi
  }

  case class Rectangle(a: Int, b: Int) extends Rectangular {
    override def height= a

    override def width = b
  }

  case class Square(a: Int) extends Rectangular {
    override def height= a

    override def width = a

  }

  object Draw {
    def apply(shape: Shape) = shape match {
      case r: Rectangle => s"A rectangle of width ${r.width}cm and height ${r.height}cm"
      case s: Square => s"A square of side ${s.width}cm"
      case c: Circle => s"A circle of radius ${c.radius}cm"
    }

  }

  println(Draw(Circle(10)))

  println(Draw(Rectangle(3, 4)))

  println(Draw(Square(4)))


}
