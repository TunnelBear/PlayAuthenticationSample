package controllers.models

object User {
  val users = List(
    User("bob@tunnelbear.com", "password", true, 10),
    User("alice@tunnelbear.com", "password", true, 0),
    User("jane@tunnelbear.com", "password", false, 10)
  )
  def find(username: String):Option[User] = users.filter(_.username == username).headOption
}

case class User(username:String, password:String, isPremium:Boolean, balance:Int) {
  def checkPassword(password: String): Boolean = this.password == password

}
