package org.example.metrics

class MedianIntSpecs extends SpecHelper {

  describe("MedianInt") {
    describe("when empty") {
      it("median is Double.NaN") {
        val medianInt = MedianInt()
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
