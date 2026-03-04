package com.stream.controller
import com.stream.actor.ActorProvider
import com.stream.database.model.*
import com.stream.service.auth.AuthService
import io.circe.Json

import scala.concurrent.Future

class AuthController(actors : ActorProvider,authService : AuthService)
{
  def registerCustomer(data: RegisterRequest): Future[Json] = {
    authService.registerCustomer(data)
  }
}
