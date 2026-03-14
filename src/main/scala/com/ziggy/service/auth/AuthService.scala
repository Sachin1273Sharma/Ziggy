package com.ziggy.service.auth


import com.ziggy.database.model.*
import com.ziggy.service.DbService
import com.ziggy.utils.AppConfig
import com.ziggy.utils.security.{PasswordHasher, ZiggySecurity}
import io.circe.Json

import java.util.UUID
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.*

@Singleton
class AuthService @Inject(dbService: DbService,security : ZiggySecurity)(implicit ec : ExecutionContext) {

  def registerCustomer(data: RegisterRequest) : Future[Json]  = {
    dbService.findUserByEmail(data.email).transformWith {
      case Success(response) => {
        response match {
          case Some(customer) => {
            Future(Json.fromString("Customer is already registered with this email. Please sign in"))
          }
          case None => {
            register(data).map(x => Json.fromString("Thank you for joining Ziggy."))
              .recover{case e => Json.fromString("We will get back to you soon") }
          }
        }
      }
    }
  }

  private def register(data: RegisterRequest) : Future[UUID] = {
    val registrationData = User(
      name = data.name,
      email = Some(data.email),
      isActive = Some(true),
      passwordHash = Some(PasswordHasher.hashPassword(data.password))
    )
    dbService.register(registrationData)
  }

  def loginUser(data: LoginRequest): Future[(Boolean, String)] = {
    dbService.findUserByEmail(data.email) map {
      case Some(customer) => {
        if (PasswordHasher.checkPassword(data.password, customer.passwordHash.getOrElse(""))) {
          (true, security.createToken(customer.id.toString, AppConfig.getString("jwt.secret")))
        } else (false, "Wrong password")
      }
      case None => {
        (false, "User not found . Please Signup")
      }
    }
  }
}