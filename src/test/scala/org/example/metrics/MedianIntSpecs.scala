package org.example.metrics

class MedianIntSpecs extends SpecHelper {

  describe("MedianInt") {
    describe("when empty") {
      it("median is Double.NaN") {
        val medianInt = MedianInt()
        assert(medianInt.median.isNaN)
      }
    }

    describe("with 1 to 9") {
      it("median is 5.0") {
        val medianInt = MedianInt()
        (1 to 9).foreach { x => medianInt.update(x) }
        assert(medianInt.median == 5.0)
      }
    }

    describe("with 1 to 10") {
      it("median is 5.5") {
        val medianInt = MedianInt()
        (1 to 10).foreach { x => medianInt.update(x) }
        assert(medianInt.median == 5.5)
      }
    }

  }
}
