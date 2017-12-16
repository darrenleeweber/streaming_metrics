import scala.io.Source

//# Computation of Report Metrics
//
//## Background:
//
// We would like to start building a general reporting framework to compute basic metrics, such
// as median or total records processed from an IO source.
//
// To make the problem more concrete, We are given a CSV file with a list of user data, specifically,
// the user id, age and favorite color. For these records, we would like to compute several metrics
// (defined below) and return a summary. In general, we don't know the file size, or the number of
// streamed records that will be provided (and it might not fit into memory).
//
//**The solution should be either in python or scala**. You should restrict your solution to use on
//  the **standard library of the language chosen**.
//
//## Requirements
//
//- Approximate constant memory usage for computation of reports
//  (i.e., can't load the data all into memory at one time)
//- Iterate exactly ONCE over the file/data
//- Compute the following metrics and return a summary datastructure from your API
//  - Total number of users processed
//  - max age of all users
//  - min age of all users
//  - Mean age of all users
//  - Median age of all users
//  - Top 5 favorite colors
//  - Total number of users processed with age greater than 21
//  - max age of users with an age greater than 21
//  - min age of users with an age greater than 21
//  - Mean age of all users with age greater than 21
//  - Median age of all users with age greater than 21
//  - Top 5 favorite colors of user with age greater than 21
//
// Reminder, the data/file can only be iterated over ONCE to compute all metrics and
// memory consumption remain approximately constant.
//
// Test data is provided in "users.csv". However, in general, the datasource could be streamed from
// another source (database). We will use a range of different input files sizes (1MB to 10GB) to
// test the submitted solution.

// A result from this scala solution that returns valid JSON
// - note that some colors are converted to lower-case and some spelling conversion is done
//$ cat users.csv | scala user_metrics.scala
//{"users": {
//  "age": {
//  "count": 1000,
//  "min": 1,
//  "max": 80,
//  "mean": 41.0
//},
//  "colors": [ "red", "gray", "purple", "grape", "charcoal" ]
//},"adults": {
//  "age": {
//  "count": 760,
//  "min": 22,
//  "max": 80,
//  "mean": 51.0
//},
//  "colors": [ "red", "gray", "purple", "grape", "charcoal" ]
//}}



//## Future Iteration and Discussion Points
//
//- Could your API be extended to support searching over any field (e.g., user id, color) or fields?
//  - requires CLI options
//  - depends what you mean by "searching"; more information is required to clearly define the requirements
//  - if it is anything like the filtering based on age > 21, that's possible for other fields too
//    - these filters could be incorporated into the Stats class
//    - the filters could either create additional data *group-by* data or *select* only the filtered results
//- What is your recommended solution to communicate intermediate summaries? For example, after every 10000 records.
//  - See code comment in the main() method, could be enabled by a CLI option
//- How would we generalize this reporting API to other Record types, with different fields, such as a
//  Car with VIN, brand, purchased at, etc...
//  - a refactoring process would be applied to adjust for the new requirements
//  - currently, the headers are not used, but they might be used to define a dynamic data type
//    - with a CSV input, dynamic data types are not likely to be correct
//    - with alternative sources, dynamic data types might be possible
//  - it likely involves creating a new class for the data entity (Car) and associated stats classes
//  - once the additional classes are created, possibilities to identify OOP abstractions will arise
//  - it would be ideal to look for opportunities to define common interfaces and use dependency injection
//    - in this exerice, the interface forming includes #report, #toJSON, and #update methods
//    - the Stats class could accept a list of any type conforming to that interface (trait)
//  - in this exercise, any attempts at premature abstractions were avoided
//- How would communicate errors with potentially malformed data from the IO source?
//  - raise and handle exceptions (none have been coded in this exercise)
//  - log informative (semantic) exception messages

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
