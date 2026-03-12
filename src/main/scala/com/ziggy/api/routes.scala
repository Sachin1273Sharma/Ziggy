package com.ziggy.api

import org.apache.pekko.http.scaladsl.server.Directives.*
import org.apache.pekko.http.scaladsl.server.Route
import com.ziggy.controller.OrderController
import com.ziggy.service.OrderDetails
import io.circe.generic.auto.*
import com.ziggy.api.AuthRoutes

import javax.inject.{Inject, Singleton}
@Singleton
class routes @Inject() (authRoutes : AuthRoutes,
                        orderRoutes : OrderRoutes) extends JsonSupport
{
  val routes: Route =
    pathPrefix("api"){
      concat(
        path("health"){
          complete("Service is up and running")
        },
        {
          authRoutes.routes
        },
        {
          orderRoutes.routes
        }
      )
    }
}