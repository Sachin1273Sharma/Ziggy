package com.stream.service.auth

import com.stream.database.model.*
import com.stream.service.DbService
import io.circe.{Json, JsonObject}

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}
import scala.util.*


class AuthService(dbService: DbService)(implicit ec : ExecutionContext) {

  def registerCustomer(data: RegisterRequest) : Future[Json]  = {
    dbService.findCustomerByEmail(data).transformWith {
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
    val registrationData = Customer(
      name = data.name,
      email = Some(data.email),
      isActive = Some(true)
    )
    dbService.register(registrationData)
  }
}