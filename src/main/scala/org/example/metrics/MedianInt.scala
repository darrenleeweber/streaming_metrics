package org.example.metrics

case class MedianInt() {
  val vals = new collection.mutable.ListBuffer[Int]

  def median(): Double = {
    if (vals.isEmpty) return Double.NaN
    val (lower, upper) = vals.sorted.splitAt(vals.size / 2)
    if (vals.size % 2 == 0) {
      (lower.last.toDouble + upper.head.toDouble) / 2.0
    } else {
      upper.head.toDouble
    }
  }

  def update(value:Int) : Unit = {
    vals += value
  }
}
