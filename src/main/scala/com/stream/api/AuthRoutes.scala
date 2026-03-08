package com.stream.api

import io.circe.generic.auto.*
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.server.Route
import com.stream.controller.AuthController
import com.stream.database.model.*
import com.stream.service.DbService
import com.stream.utils.security.ZiggySecurity

import javax.inject.*
import scala.concurrent.ExecutionContext



@Singleton
class AuthRoutes @Inject (val authController: AuthController,
                          val db: DbService
                          )(implicit ec : ExecutionContext) extends ZiggySecurity(db)(ec) with JsonSupport {
  val routes: Route =
    pathPrefix("auth") {
      concat(
        path("register") {
          entity(as[RegisterRequest]) { data => {
            complete(StatusCodes.Continue, authController.registerCustomer(data))
          }
          }
        },
        path("login") {
          authenticateOAuth2Async[Customer]("UnAuthorized",creds => validateLoginCredentials(creds)) {
            customer : Customer => post {
                complete(StatusCodes.OK,s"Welcome ${customer.name}")
            }
          }
        }
      )
    }
}