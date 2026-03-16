package com.ziggy.controller
import org.apache.pekko.http.scaladsl.model.headers.HttpCookie
import com.ziggy.actor.ActorProvider
import com.ziggy.database.model.*
import com.ziggy.service.auth.AuthService
import io.circe.Json

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AuthController@Inject (
                             authService : AuthService) (using ec : ExecutionContext)
{
  def registerCustomer(data: RegisterRequest): Future[Json] = {
    authService.registerCustomer(data)
  }

  def login(data : LoginRequest): Future[Either[String,HttpCookie]] = {
    authService.loginUser(data) map {
      case (true,token) => Right(HttpCookie(
        name = "AUTH_TOKEN",
        value = token,
        maxAge = Some(3600.toLong),
        path = Some("/"),
        secure = false,
        httpOnly = true
      ))
      case (false,message) => Left(message)
    }
  }
}
