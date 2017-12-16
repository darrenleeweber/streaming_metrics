import scala.io.Source

object UserMetrics {

  case class User(id:Int, age:Int, color:String)

  def parseColor(color:String) : String = {
    var col = color.toLowerCase()
    // US spellings
    if(col == "grey") col = "gray"
    col
  }

  def parseUserCSV(line:String) : User = {
    // id, age, color
    val data = line.split(',').map(_.trim)
    User(data(0).toInt, data(1).toInt, parseColor(data(2)))
  }

  trait Reporter {
    def report() : Unit
    def toJSON() : String
  }

  trait Accumulator[DATA] extends Reporter {
    def update(data: DATA) : Unit
  }

  case class AgeStats(var min:Int = 999, var max:Int = -999, var sum:Int = 0, var count:Int = 0)
    extends Accumulator[Int] {

    def mean() : Double = {
      if(count == 0) return Double.NaN
      sum / count
    }

    def report() : Unit = {
      println("Count:\t" + count)
      println("Age:")
      println("\tmin:\t" + min)
      println("\tmax:\t" + max)
      println("\tmean:\t" + mean)
      // println("median:\t" + median)
      println()
    }

    def toJSON() : String = {
      s"""{
         |      "count": ${count},
         |      "min": ${min},
         |      "max": ${max},
         |      "mean": ${mean}
         |    }""".stripMargin
    }

    def update(age: Int) : Unit = {
      count += 1
      sum += age
      if(age > max) max = age
      if(age < min) min = age
      // median is not calculated without accumulating all values in a stream process,
      // otherwise constant memory constraint could be breached;
      // median calculation requires a constant-memory stream algorithm - does one exist?
    }
  }

  case class ColorStats(private val colors:collection.mutable.Map[String, Int] = collection.mutable.Map())
    extends Accumulator[String] {
    // Top 5 favorite colors - regardless of ties
    def favorites() : scala.collection.immutable.ListMap[String,Int] = {
      sorted.slice(0, 5)
    }

    def report() : Unit = {
      println("Favorite Colors:")
      println(favorites().mkString("\t", "\n\t", "\n"))
    }

    def sorted() : collection.immutable.ListMap[String, Int] = {
      collection.immutable.ListMap(colors.toSeq.sortWith(_._2 > _._2):_*)
    }

    def toJSON() : String = {
      s"""[ ${favorites().keys.mkString("\"", "\", \"", "\"")} ]"""
    }

    def update(color: String) : Unit = {
      colors(color) = colors.getOrElse(color, 0) + 1
    }
  }

  class UserStats(title:String = "users", age:AgeStats = AgeStats(), colors:ColorStats = ColorStats())
    extends Accumulator[User] {
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

  class AdultUserStats(title:String = "adults", age:AgeStats = AgeStats(), colors:ColorStats = ColorStats())
    extends UserStats(title, age, colors) {

    override def update(user: User) : Unit = {
      if(user.age <= 21) return
      age.update(user.age)
      colors.update(user.color)
    }
  }

  case class Stats(models:List[Accumulator[User]])
    extends Accumulator[User] {
    def report() : Unit = {
      for(model <- models) model.report()
    }

    def toJSON() : String = {
      models.map(_.toJSON).mkString("{", ",", "}")
    }

    def update(user: User) : Unit = {
      for(model <- models) model.update(user)
    }
  }

  def main(args: Array[String]): Unit = {
    // Source.fromFile() is an option if CLI is created
    val source = Source.stdin
    val stats = Stats(List(new UserStats(), new AdultUserStats()))

    var headers : Array[String] = Array()
    var index = 0

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
