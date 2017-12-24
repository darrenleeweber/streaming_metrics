package org.example.metrics

class MedianIntBinnedSpecs extends SpecHelper {

  describe("MedianIntBinned") {
    describe("when empty") {
      it("median is Double.NaN") {
        val medianInt = MedianIntBinned(2)
        assert(medianInt.median.isNaN)
      }
    }

    // should behave exactly like org.example.metrics.MedianInt
    describe("with 1 to 9 and interval is 1") {
      it("median is 5.0") {
        val medianInt = MedianIntBinned(1)
        (1 to 9).foreach { x => medianInt.update(x) }
        assert(medianInt.median == 5.0)
      }
    }

    // should behave exactly like org.example.metrics.MedianInt
    describe("with 1 to 10 and interval is 1") {
      it("median is 5.5") {
        val medianInt = MedianIntBinned(1)
        (1 to 10).foreach { x => medianInt.update(x) }
        assert(medianInt.median == 5.5)
      }
    }

    // should behave exactly like org.example.metrics.MedianInt, i.e.
    // val medianInt = org.example.metrics.MedianInt()
    // (1 to 10).map {x => x - (x % 2) }.foreach {x => medianInt.update(x) }
    // medianInt.median => Double = 5.0
    describe("with 1 to 10 and interval is 2") {
      it("median is 5.0") {
        val medianInt = MedianIntBinned(2)
        (1 to 10).foreach { x => medianInt.update(x) }
        assert(medianInt.median == 5.0)
      }
    }

  }
}
