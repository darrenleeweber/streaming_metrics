package org.example.metrics

case class MedianInt() extends Median[Int] {
  private val vals = new collection.mutable.ListBuffer[Int]

  override def median : Double = {
    if (vals.isEmpty) {
      Double.NaN
    } else if (vals.lengthCompare(1) == 0) {
      vals.head.toDouble
    } else {
      val size = vals.size
      val (lower, upper) = vals.sorted.splitAt(size / 2)
      if (size % 2 == 0) {
        (lower.last.toDouble + upper.head.toDouble) / 2.0
      } else {
        upper.head.toDouble
      }
    }
  }

  override def update(value:Int) : Unit = {
    vals += value
  }
}
