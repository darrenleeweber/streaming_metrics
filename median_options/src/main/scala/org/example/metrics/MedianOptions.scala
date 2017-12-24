package org.example.metrics

object MedianOptions {
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
