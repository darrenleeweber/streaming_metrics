package org.example.metrics

class UserStats(title:String = "users") extends Accumulator[User] {

  protected val age:IntStats = IntStats("age")
  protected val colors:ColorFavorites = ColorFavorites()

  def report() : Unit = {
    println(title)
    age.report()
    colors.report()
    println
  }

  def toJSON() : String = {
    s""""${title}": {
       |    "age": ${age.toJSON},
       |    "colors": ${colors.toJSON}
       |}""".stripMargin
  }

  def update(user: User) : Unit = {
    age.update(user.age)
    colors.update(user.color)
  }
}
