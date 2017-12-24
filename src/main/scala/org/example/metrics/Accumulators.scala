package org.example.metrics

trait Accumulators[T] extends Accumulator[T] {

  val models:List[Accumulator[T]]

  def report() : Unit = {
    for(model <- models) model.report()
  }

  def toJSON() : String = {
    models.map(_.toJSON).mkString("{", ",", "}")
  }

  def update(data: T) : Unit = {
    for(model <- models) model.update(data)
  }
}
