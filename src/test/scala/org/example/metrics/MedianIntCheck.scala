package org.example.metrics

import org.scalacheck.Properties
import org.scalacheck.Prop.forAll

object MedianIntCheck extends Properties("MedianInt") {

  property("median:Double") = forAll { (lst: List[Int]) =>
    val medianInt = MedianInt()
    lst.foreach { i => medianInt.update(i) }
    val median = medianInt.median
    if (lst.isEmpty) {
      median.isNaN
    } else if (lst.lengthCompare(1) == 0) {
      median == lst.head
    } else {
      lst.min <= median && median <= lst.max
    }
  }

}