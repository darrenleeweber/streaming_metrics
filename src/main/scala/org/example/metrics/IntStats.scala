package org.example.metrics

case class IntStats(title:String,
                    var count:Int = 0,
                    var min:Int = 999,
                    var max:Int = -999,
                    var sum:Int = 0)
  extends NumericStats[Int] {

  override def mean(): Double = {
    if (count == 0) {
      Double.NaN
    } else {
      sum.toDouble / count.toDouble
    }
  }

  // Use the MedianIntBinned to conserve memory, at the expense of accuracy
  private val medianInt = MedianIntBinned(5)
  override def median(): Double = medianInt.median

  override def update(value: Int) : Unit = {
    count += 1
    sum += value
    if(value > max) max = value
    if(value < min) min = value
    medianInt.update(value)
  }
}
