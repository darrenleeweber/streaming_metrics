package org.example.metrics

import scala.io.Source

object UserMetrics {

  def parseUserCSV(line:String) : User = {
    // id, age, color
    val data = line.split(',').map(_.trim)
    User(data(0).toInt, data(1).toInt, data(2))
  }

  def getSource(args:Array[String]) : Source = {
    if (args.length > 0) {
      Source.fromFile(args(0))
    } else {
      Source.stdin
    }
  }

  def main(args:Array[String]) : Unit = {
    val stats = UserAccumulators(List(new UserStats(), new UserAdultStats()))
    var index = 0
    val source = getSource(args)
    for (line <- source.getLines) {
      // skip header at index == 0
      if (index > 0) {
        stats.update(parseUserCSV(line))
        // scalastyle:off
        // for intermediate summaries
        //if (index % 500 == 0) println(stats.toJSON)
        // scalastyle:on
      }
      index += 1
    }
    println(stats.toJSON) // scalastyle:ignore
  }

}
