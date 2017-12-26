package org.example.metrics

case class IntStats(title:String, medianInt:Median[Int] = MedianInt())
  extends NumericStats[Int] {

  var min:Int = Int.MaxValue
  var max:Int = Int.MinValue

  var count:Int = 0
  var sum:Int = 0

  override def mean : Double = {
    if (count == 0) {
      Double.NaN
    } else {
      sum.toDouble / count.toDouble
    }
  }

  override def median : Double = medianInt.median

  override def update(value:Int) : Unit = {
    count += 1
    sum += value
    if(value > max) max = value
    if(value < min) min = value
    medianInt.update(value)
  }
}
