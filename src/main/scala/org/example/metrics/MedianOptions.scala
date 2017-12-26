package org.example.metrics

object MedianOptions {
  def main(args:Array[String]) : Unit = {
    val BinSize = 5
    val medianIntBinned = MedianIntBinned(BinSize)
    val medianInt = MedianInt()
    val ValuesN = 21
    val ValuesRange = 100
    val values = Seq.fill(ValuesN)(util.Random.nextInt(ValuesRange))
    for (i <- values) {
      medianInt.update(i)
      medianIntBinned.update(i)
    }
    // scalastyle:off
    println(values.sorted)
    println(medianInt.median)
    println(medianIntBinned.median)
    // scalastyle:on
  }
}
