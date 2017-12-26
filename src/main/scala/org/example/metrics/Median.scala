package org.example.metrics

trait Median[T] {

  def median : Double

  def update(value:T) : Unit

}
