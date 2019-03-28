package lab2

// Theoretical questions: why do we need abstraction
// How `traits` in Scala are used?

// It helps encapsulate behavior. It helps decouple software elements. It helps having more self-contained modules.

// Traits are used to share interfaces and fields between classes. They are similar to Java 8’s interfaces.
// Classes and objects can extend traits but traits cannot be instantiated and therefore have no parameters.

trait Animal {
  // Is this abstract or concrete (implemented) member?
  // abstract
  def name: String

  // Is this abstract or concrete (implemented) member?
  // abstract
  def makeSound(): String
}

trait Walks {

  // What does this line mean?
  // we inherit from Animal trait, in order to use it's members

  this: Animal =>

  // Is this abstract or concrete (implemented) member?
  // It is an concrete member, extended and implemented from Animal class
  // Why `name` parameter is available here?
  // In constructor we used Animal, all member can be used by Walks
  def walk: String = s"$name is walking"

}


// Can Dog only extend from `Walks`?
// Try to fix Dog, so it extends proper traits
// Implement Dog class so it passes tests
case class Dog(dog_name: String) extends Animal with Walks {
  override def makeSound(): String = "Whooof"

  override def name: String = dog_name
}


// Implement Cat class so it passes tests
case class Cat(cat_name: String) extends Animal with Walks {
  override def makeSound(): String = "Miiyaaau"

  override def name: String = cat_name
}

object Lab2 extends App {

  // Here we will test Dog and Cat classes

  val dog1 = Dog("Ceasar")
  val dog2 = Dog("Laika")

  assert(dog1.name == "Ceasar")
  assert(dog2.name == "Laika")

  assert(dog1.makeSound() == "Whooof")
  assert(dog2.makeSound() == "Whooof")

  assert(dog1.walk == "Ceasar is walking")
  assert(dog2.walk == "Laika is walking")

  val cat1 = Cat("Tosha")
  val cat2 = Cat("Chocolate")

  assert(cat1.name == "Tosha")
  assert(cat2.name == "Chocolate")

  assert(cat1.makeSound() == "Miiyaaau")
  assert(cat2.makeSound() == "Miiyaaau")

  assert(cat1.walk == "Tosha is walking")
  assert(cat2.walk == "Chocolate is walking")

}