package org.example.metrics

case class MedianInt() {
  private val vals = new collection.mutable.ListBuffer[Int]

  def median(): Double = {
    if (vals.isEmpty) return Double.NaN
    if (vals.lengthCompare(1) == 0) return vals.head.toDouble
    val size = vals.size
    val (lower, upper) = vals.sorted.splitAt(size / 2)
    if (size % 2 == 0) {
      (lower.last.toDouble + upper.head.toDouble) / 2.0
    } else {
      upper.head.toDouble
    }
  }

  def update(value:Int) : Unit = {
    vals += value
  }
}
