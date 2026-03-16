package com.ziggy.api

import org.apache.pekko.http.scaladsl.server.Directives.*
import org.apache.pekko.http.scaladsl.server.Route
import javax.inject.{Inject, Singleton}
@Singleton
class routes @Inject() (authRoutes : AuthRoutes,
                        orderRoutes : OrderRoutes,
                        utilRoutes: UtilRoutes) extends JsonSupport
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
        },
        {
          utilRoutes.utilityRoutes
        }
      )
    }
}
