package controllers

import play.api.mvc.{Action, RequestHeader, Result, Controller}
import models.User


trait Authentication {
  self:Controller =>
  var accessConditions: List[Conditions.Condition] = List.empty

  def AuthenticateMe(f: User => Result) = Action { implicit request =>
    val user = AuthUtils.parseUserFromRequest

    if(user.isEmpty)
      Forbidden("Invalid username or password")
    else {
      accessConditions.map(condition => condition(user.get)).collectFirst[String]{case Left(error) => error}
      match {
        case Some(error) => Forbidden(s"Conditions not met: $error")
        case _ => f(user.get)
      }
    }
  }
}

object Conditions {
  type Condition = (User => Either[String, Unit])
  def isPremiumUser:Condition = {
    user => if(user.isPremium)
      Right()
    else
      Left("User must be premium")
  }


  def balanceGreaterThan(required:Int):Condition = {
    user => if(user.balance > required)
      Right()
    else
      Left(s"User balance must be > $required")
  }
}

trait PremiumUsersOnly {
  self:Authentication =>
  accessConditions = accessConditions :+ Conditions.isPremiumUser
}

trait BalanceCheck {
  self:Authentication =>
  def getRequiredBalance:Int
  accessConditions = accessConditions :+ Conditions.balanceGreaterThan(getRequiredBalance)
}

object AuthUtils {
  def parseUserFromCookie(implicit request: RequestHeader) = request.session.get("username").flatMap(username => User.find(username))

  def parseUserFromQueryString(implicit request:RequestHeader) = {
    val query = request.queryString.map { case (k, v) => k -> v.mkString }
    val username = query get ("username")
    val password = query get ("password")
    (username, password) match {
      case (Some(u), Some(p)) => User.find(u).filter(user => user.checkPassword(p))
      case _ => None
    }
  }

  def parseUserFromRequest(implicit request:RequestHeader):Option[User] = {
    parseUserFromQueryString orElse  parseUserFromCookie
  }

}
