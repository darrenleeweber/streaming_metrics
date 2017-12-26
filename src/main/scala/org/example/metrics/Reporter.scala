package org.example.metrics

trait Reporter {
  def report : String

  def toJSON : String
}
