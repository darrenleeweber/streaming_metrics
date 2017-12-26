package org.example.metrics

trait Accumulator[T] extends Reporter {
  def update(data:T) : Unit
}

