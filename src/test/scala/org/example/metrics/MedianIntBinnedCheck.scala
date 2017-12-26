package org.example.metrics

import org.scalacheck.Properties
import org.scalacheck.Prop.forAll

object MedianIntBinnedCheck extends Properties("MedianIntBinned") {

  property("median:Double") = forAll { (lst: List[Int]) =>
    val medianInt = MedianIntBinned(1)
    lst.foreach { i => medianInt.update(i) }
    val median = medianInt.median
    if (lst.isEmpty) {
      median.isNaN
    } else if (lst.lengthCompare(1) == 0) {
      median == lst.head.toDouble
    } else {
      lst.min <= median && median <= lst.max
    }
  }

  property("median:Binned") = forAll { (lst: List[Int]) =>
    val binInterval = 2
    val medianInt = MedianIntBinned(binInterval)
    lst.foreach { i => medianInt.update(i) }
    val median = medianInt.median
    if (lst.isEmpty) {
      median.isNaN
    } else if (lst.lengthCompare(1) == 0) {
      median == (lst.head - (lst.head % binInterval)).toDouble
    } else {
      lst.min <= median && median <= lst.max
    }
  }

}