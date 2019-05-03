package lab6.model

case class StudentModel(id: Int, name: String, surname: String, birthYear: Int)

case class StudentModelPostPut(name: String, surname: String, birthYear: Int)
