package com.stream.api

import akka.http.scaladsl.server.Directives.pathPrefix


class AuthRoutes extends JsonSupport {
  
  val routes =
    pathPrefix("api") {
      path("register") {
        post {
          entity
        }
      }
    }
}