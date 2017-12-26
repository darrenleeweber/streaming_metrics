package org.example.metrics

class UserStats(title:String = "users") extends Accumulator[User] {

  // Use the MedianIntBinned to conserve memory, at the expense of accuracy
  // Bin ages into 5 year bins
  protected val AgeBin = 5
  protected val medianInt = MedianIntBinned(AgeBin)
  protected val age:IntStats = IntStats("age", medianInt)

  protected val FavoriteN = 5
  protected val colors:ColorFavorites = ColorFavorites(FavoriteN)

  def report : String = {
    s"$title\n" + age.report + colors.report
  }

  def toJSON : String = {
    s""""$title": {
       |    "age": ${age.toJSON},
       |    "colors": ${colors.toJSON}
       |}""".stripMargin
  }

  def update(user: User) : Unit = {
    age.update(user.age)
    colors.update(user.color)
  }
}
