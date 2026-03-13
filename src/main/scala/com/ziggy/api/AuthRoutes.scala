package com.ziggy.api

import io.circe.generic.auto.*
import org.apache.pekko.http.scaladsl.model.StatusCodes
import org.apache.pekko.http.scaladsl.server.Directives.*
import org.apache.pekko.http.scaladsl.server.Route
import com.ziggy.controller.AuthController
import com.ziggy.database.model.*
import com.ziggy.service.DbService
import com.ziggy.utils.security.ZiggySecurity

import javax.inject.*
import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}



@Singleton
class AuthRoutes @Inject (val authController: AuthController,
                          val db: DbService
                          )(using ec : ExecutionContext) extends ZiggySecurity(db)(ec) with JsonSupport {
  val routes: Route =
    pathPrefix("auth") {
      concat(
        path("register") {
          entity(as[RegisterRequest]) { data => {
            complete(StatusCodes.Continue, authController.registerCustomer(data))
          }
          }
        },
        path("test") {
          authenticateOAuth2Async[User]("Unauthorized",validateLoginCredentials) {
            customer  => get {
                complete(StatusCodes.OK,s"Welcome ${customer.name}")
            }
          }
        },
        path("login") {
          entity(as[LoginRequest]) {
            data => post {
              onComplete(authController.login(data)) {
                case Success(Right(value)) => {
                  setCookie(value){
                    complete(StatusCodes.OK,"Successfully loggedIn")
                  }
                }
                case Success(Left(message)) => {
                  complete(StatusCodes.Conflict,message)
                }
                case Failure(ex)   => complete(StatusCodes.InternalServerError,ex.getMessage)
              }
            }
          }
        }
      )
    }
}