package com.stream.api

import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.server.Route
import com.stream.controller.OrderController
import com.stream.service.OrderDetails
import io.circe.generic.auto.*
import com.stream.api.AuthRoutes

class routes(authRoutes : AuthRoutes) extends JsonSupport
{
  val routes: Route =
    pathPrefix("api"){
      concat(
        path("health"){
          complete("Service is up and running")
        },
        {
          authRoutes.routes
        }
      )
    }
}