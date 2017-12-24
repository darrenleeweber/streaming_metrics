package org.example.metrics

trait NumericStats[T] extends Accumulator[T] {
  def title:String
  def count:T
  def min:T
  def max:T
  def sum:T

  def mean(): Double

  def median(): Double

  def report(): Unit = {
    println("Count:\t" + count)
    println(s"${title}:")
    println("\tmin:\t" + min)
    println("\tmax:\t" + max)
    println("\tmean:\t" + mean())
    println("\tmedian:\t" + median())
    println()
  }

  def toJSON(): String = {
    s"""{
       |      "count": ${count},
       |      "min": ${min},
       |      "max": ${max},
       |      "mean": ${mean()}
       |      "median": ${median()}
       |    }""".stripMargin
  }
}

