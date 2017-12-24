package org.example.metrics

case class UserAccumulators(models:List[Accumulator[User]]) extends Accumulators[User] {}
