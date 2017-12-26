package org.example.metrics

trait NumericStats[T] extends Accumulator[T] {
  def title:String
  def count:T
  def min:T
  def max:T
  def sum:T

  def mean : Double

  def median : Double

  def report : String = {
    s"""
       |$title:
       |\tcount:\t$count
       |\tmin:\t$min
       |\tmax:\t$max
       |\tmean:\t$mean
       |\tmedian:\t$median
       |""".stripMargin
  }

  def toJSON : String = {
    s"""{
       |      "count": $count,
       |      "min": $min,
       |      "max": $max,
       |      "mean": $mean
       |      "median": $median
       |    }""".stripMargin
  }
}
