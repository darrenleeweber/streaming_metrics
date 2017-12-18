
object MedianOptions {

  case class MedianInt() {
    val vals = collection.mutable.ListBuffer[Int]()

    def median(): Double = {
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

  def main(args: Array[String]): Unit = {
    val medianIntBinned = MedianIntBinned(5)
    val medianInt = MedianInt()
    val values = Seq.fill(21)(util.Random.nextInt(100))
    for (i <- values) {
      medianInt.update(i)
      medianIntBinned.update(i)
    }
    println(values.sorted)
    println(medianInt.median)
    println(medianIntBinned.median)
  }

}
