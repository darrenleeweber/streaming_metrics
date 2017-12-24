package org.example.metrics

trait Reporter {
  def report(): Unit

  def toJSON(): String
}
