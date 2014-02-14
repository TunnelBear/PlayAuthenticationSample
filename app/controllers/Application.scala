package controllers

import play.api.mvc._


object PaidApplication extends Controller with Authentication with PremiumUsersOnly with BalanceCheck {

  override def getRequiredBalance = 8

  def helloUser = AuthenticateMe {
    user => Ok(s"hello ${user.username}")
  }

}


