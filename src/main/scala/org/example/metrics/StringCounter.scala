package org.example.metrics

trait StringCounter extends Accumulator[String] {
  val strings:collection.mutable.Map[String, Int] = collection.mutable.Map()

  def sorted : collection.immutable.ListMap[String, Int] = {
    collection.immutable.ListMap(strings.toSeq.sortWith(_._2 > _._2):_*)
  }

  def toJSON : String = {
    s"""[ ${strings.keys.mkString("\"", "\", \"", "\"")} ]"""
  }

  def update(string:String) : Unit = {
    strings(string) = strings.getOrElse(string, 0) + 1
  }
}

