package org.example.metrics

import org.scalacheck.Properties
import org.scalacheck.Prop.forAll

object MedianIntBinnedCheck extends Properties("MedianIntBinned") {

  property("median:Double") = forAll { (lst: List[Int]) =>
    val medianInt = MedianIntBinned(1)
    lst.foreach { i => medianInt.update(i) }
    val median = medianInt.median()
    if (lst.isEmpty) {
      median.isNaN
    } else {
      lst.min <= median && median <= lst.max
    }
  }

  // TODO: use a binInterval > 1

}