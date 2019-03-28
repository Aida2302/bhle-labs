package lab3

object Boot1 extends App {

  sealed trait IntList {
    def length: Int = {
      def calculateLength(intList: IntList, acc: Int = 0): Int = intList match {
        case End => acc
        case Node(_, tail) => calculateLength(tail, acc + 1)
      }
      calculateLength(this)
    }

    def product: Int = {
      def calculateProduct(intList: IntList, acc: Int = 1): Int = intList match {
        case End => acc
        case Node(head, tail) => calculateProduct(tail, acc * head)
      }
      calculateProduct(this)
    }

    def double: IntList = {
      def doubleList(intList: IntList): IntList = intList match {
        case End => End
        case Node(head, tail) => Node(head * 2, doubleList(tail))
      }
      doubleList(this)
    }


  }
  case object End extends IntList
  case class Node(head: Int, tail: IntList) extends IntList


  // Tail recursion


  val intList = Node(1, Node(2, Node(3, Node(4, End))))

  assert(intList.length == 4)
  assert(intList.tail.length == 3)
  assert(End.length == 0)


  val intList2 = Node(1, Node(2, Node(3, Node(4, End))))

  assert(intList2.product == 1 * 2 * 3 * 4)
  assert(intList2.tail.product == 2 * 3 * 4)
  assert(End.product == 1)


  val intList3 = Node(1, Node(2, Node(3, Node(4, End))))

  assert(intList3.double == Node(1 * 2, Node(2 * 2, Node(3 * 2, Node(4 * 2, End)))))
  assert(intList3.tail.double == Node(4, Node(6, Node(8, End))))
  assert(End.double == End)

}
