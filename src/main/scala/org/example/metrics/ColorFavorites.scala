package org.example.metrics

case class ColorFavorites() extends StringCounter {
  // Top 5 favorite colors - regardless of ties
  def favorites() : scala.collection.immutable.ListMap[String,Int] = {
    sorted.slice(0, 5)
  }

  override def report() : Unit = {
    println("Favorite Colors:")
    println(favorites.mkString("\t", "\n\t", "\n"))
  }

  override def toJSON() : String = {
    s"""[ ${favorites.keys.mkString("\"", "\", \"", "\"")} ]"""
  }

  override def update(string: String) : Unit = {
    val color = parseColor(string)
    strings(color) = strings.getOrElse(color, 0) + 1
  }

  def parseColor(color:String) : String = {
    var col = color.toLowerCase()
    if(col == "grey") col = "gray" // US spelling (could use a good library for this)
    // an argument could be made to map 'charcoal' -> 'gray', but that's not done here
    col
  }
}

