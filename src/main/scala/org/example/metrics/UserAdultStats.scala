package org.example.metrics

class UserAdultStats(title:String = "adults") extends UserStats(title) {
  override def update(user: User) : Unit = {
    if(user.age <= 21) return
    age.update(user.age)
    colors.update(user.color)
  }
}

