package org.example.metrics

// (1 to 10).map {x => x - (x % 2) } => Vector(0, 2, 2, 4, 4, 6, 6, 8, 8, 10)
// MedianIntBinned(2) represents this vector as a map with value:count pairs,
// which it sorts by value before calculating the median, i.e. sorted:
// ArrayBuffer((0,1), (2,2), (4,2), (6,2), (8,2), (10,1))

// Should behave exactly like org.example.metrics.MedianInt when `binInterval` == 1,
// but this binned version would use _more_ memory.

case class MedianIntBinned(binInterval:Int) extends Median[Int] {
  private val vals = collection.mutable.Map[Int, Int]()

  def median : Double = {
    if (vals.isEmpty) {
      Double.NaN
    } else if (vals.size == 1) {
      vals.keys.head.toDouble
    } else {
      if (vals.values.sum % 2 == 0) {
        findMedianEven
      } else {
        findMedianOdd
      }
    }
  }

  def update(value:Int) : Unit = {
    // accumulate rounded values and count them in a Map
    val v = value - (value % binInterval)
    vals(v) = vals.getOrElse(v, 0) + 1
  }

  private

  def findMedianOdd : Double = {
    findValue((vals.values.sum / 2) + 1)
  }

  def findMedianEven : Double = {
    val sorted = sortedSeq
    val lowerIndex = vals.values.sum / 2
    val upperIndex = lowerIndex + 1
    val lowerValue = findValue(lowerIndex, sorted)
    val upperValue = findValue(upperIndex, sorted)
    (lowerValue + upperValue) / 2.0
  }

  def findValue(countIndex:Int, sorted:Seq[(Int, Int)] = sortedSeq) : Double = {
    var index = 0
    sorted.find {
      case(value, count) => index += count; index >= countIndex
    } match {
      case Some(value_count) => value_count._1.toDouble
      case None => Double.NaN
    }
  }

  def sortedSeq : Seq[(Int, Int)] = {
    vals.toSeq.sortWith(_._1 < _._1)
  }

}
