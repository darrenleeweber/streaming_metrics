import scala.io.Source

object UserMetrics {

  trait Reporter {
    def report(): Unit

    def toJSON(): String
  }

  trait Accumulator[T] extends Reporter {
    def update(data: T): Unit
  }

  trait Accumulators[T] extends Accumulator[T] {

    val models:List[Accumulator[T]]

    def report() : Unit = {
      for(model <- models) model.report()
    }

    def toJSON() : String = {
      models.map(_.toJSON).mkString("{", ",", "}")
    }

    def update(data: T) : Unit = {
      for(model <- models) model.update(data)
    }
  }

  trait StringCounter extends Accumulator[String] {
    val strings:collection.mutable.Map[String, Int] = collection.mutable.Map()

    def sorted() : collection.immutable.ListMap[String, Int] = {
      collection.immutable.ListMap(strings.toSeq.sortWith(_._2 > _._2):_*)
    }

    def toJSON() : String = {
      s"""[ ${strings.keys.mkString("\"", "\", \"", "\"")} ]"""
    }

    def update(string: String) : Unit = {
      strings(string) = strings.getOrElse(string, 0) + 1
    }
  }

  trait NumericStats[T] extends Accumulator[T] {
    def title:String
    def count:T
    def min:T
    def max:T
    def sum:T

    def mean(): Double

    // median is not calculated without accumulating all values in a stream process,
    // otherwise constant memory constraint could be breached;
    // median calculation requires a constant-memory stream algorithm - does one exist?
    //def median(): T

    def report(): Unit = {
      println("Count:\t" + count)
      println(s"${title}:")
      println("\tmin:\t" + min)
      println("\tmax:\t" + max)
      println("\tmean:\t" + mean())
      // println("median:\t" + median)
      println()
    }

    def toJSON(): String = {
      s"""{
         |      "count": ${count},
         |      "min": ${min},
         |      "max": ${max},
         |      "mean": ${mean()}
         |    }""".stripMargin
      // |      "median": median())
    }
  }

  case class IntStats(title:String,
                      var count:Int = 0,
                      var min:Int = 999,
                      var max:Int = -999,
                      var sum:Int = 0)
    extends NumericStats[Int] {

    override def mean(): Double = {
      if (count == 0) return Double.NaN
      sum / count
    }

    override def update(value: Int) : Unit = {
      count += 1
      sum += value
      if(value > max) max = value
      if(value < min) min = value
    }
  }

  case class ColorFavorites() extends StringCounter {
    // Top 5 favorite colors - regardless of ties
    def favorites() : scala.collection.immutable.ListMap[String,Int] = {
      sorted.slice(0, 5)
    }

    override def report() : Unit = {
      println("Favorite Colors:")
      println(favorites.mkString("\t", "\n\t", "\n"))
    }

    override def toJSON() : String = {
      s"""[ ${favorites.keys.mkString("\"", "\", \"", "\"")} ]"""
    }

    override def update(string: String) : Unit = {
      val color = parseColor(string)
      strings(color) = strings.getOrElse(color, 0) + 1
    }

    def parseColor(color:String) : String = {
      var col = color.toLowerCase()
      if(col == "grey") col = "gray" // US spelling (could use a good library for this)
      // an argument could be made to map 'charcoal' -> 'gray', but that's not done here
      col
    }
  }

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

  class AdultUserStats(title:String = "adults") extends UserStats(title) {
    override def update(user: User) : Unit = {
      if(user.age <= 21) return
      age.update(user.age)
      colors.update(user.color)
    }
  }

  case class UserAccumulators(models:List[Accumulator[User]]) extends Accumulators[User] {}

  case class User(id:Int, age:Int, color:String)

  def parseUserCSV(line:String) : User = {
    // id, age, color
    val data = line.split(',').map(_.trim)
    User(data(0).toInt, data(1).toInt, data(2))
  }

  def getSource(args: Array[String]) = {
    if (args.length > 0) {
      Source.fromFile(args(0))
    } else {
      Source.stdin
    }
  }

  def main(args: Array[String]): Unit = {
    val stats = UserAccumulators(List(new UserStats(), new AdultUserStats()))
    var headers : Array[String] = Array()
    var index = 0
    val source = getSource(args)
    for (line <- source.getLines) {
      if(index == 0) {
        headers = line.split(',').map(_.trim)
      } else {
        stats.update(parseUserCSV(line))
        // for intermediate summaries
        //if(index % 500 == 0) println(stats.toJSON())
      }
      index += 1
    }
    println(stats.toJSON())
  }

}
