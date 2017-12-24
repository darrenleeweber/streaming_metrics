package org.example.metrics

case class MedianIntBinned(binInterval:Int) {
  val vals = collection.mutable.Map[Int, Int]()

  def findMedian(): Option[(Int, Int)] = {
    val medianIndex = vals.values.sum / 2
    var index = 0
    vals.toSeq.sortWith(_._1 < _._1).find {
      case(value, count) => index += count; index > medianIndex
    }
  }

  def median(): Double = {
    if (vals.isEmpty) return Double.NaN
    findMedian() match {
      case Some(value_count) => value_count._1.toDouble
      case None => Double.NaN
    }
  }

  def update(value:Int) : Unit = {
    // accumulate rounded values and count them in a Map
    val v = value - (value % binInterval)
    vals(v) = vals.getOrElse(v, 0) + 1
  }
}
