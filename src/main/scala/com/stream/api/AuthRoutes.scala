package com.stream.api

import io.circe.generic.auto._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.server.Route
import com.stream.controller.AuthController
import com.stream.database.model.*



class AuthRoutes(authController: AuthController) extends JsonSupport {
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
          authenticateOAuth2("UnAuthorized",)

        }
      )
    }
}