package org.example.metrics

case class ColorFavorites(favoriteN:Int) extends StringCounter {

  // Top 5 favorite colors - regardless of ties
  def favorites: scala.collection.immutable.ListMap[String,Int] = {
    sorted.slice(0, favoriteN)
  }

  override def report : String = {
    "Favorite Colors:\n" + favorites.mkString("\t", "\n\t", "\n")
  }

  override def toJSON : String = {
    s"""[ ${favorites.keys.mkString("\"", "\", \"", "\"")} ]"""
  }

  override def update(string:String) : Unit = {
    val color = parseColor(string)
    strings(color) = strings.getOrElse(color, 0) + 1
  }

  def parseColor(color:String) : String = {
    spellingCorrections(color.toLowerCase())
  }

  // US spelling (could use a good library for this)
  def spellingCorrections(color:String) : String = {
    // an argument could be made to map 'charcoal' -> 'gray', but that's not done here
    if(color == "grey") {
      "gray"
    } else {
      color
    }
  }
}
