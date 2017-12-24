package org.example.metrics

class MedianIntBinnedSpecs extends SpecHelper {

  describe("MedianIntBinned") {
    describe("when empty") {
      it("median is Double.NaN") {
        val medianInt = MedianIntBinned(2)
        assert(medianInt.median.isNaN)
      }

//      it("should produce NoSuchElementException when head is invoked") {
//        assertThrows[NoSuchElementException] {
//          Set.empty.head
//        }
//      }
    }
  }
}
