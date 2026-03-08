package com.stream.utils.security

import akka.http.scaladsl.model.headers.{HttpChallenge, HttpChallenges}
import akka.http.scaladsl.server.Directives.AuthenticationResult
import akka.http.scaladsl.server.directives.{AuthenticationResult, Credentials}
import com.stream.database.model.Customer
import com.stream.service.DbService
import com.stream.utils.AppConfig
import com.stream.utils.jwt.Jwt
import io.circe.*

import javax.inject.*
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ZiggySecurity @Inject()(val dbService: DbService)(implicit ec : ExecutionContext) extends Jwt {

  /**
   * @param credentials Will provide token
   *
   */
  def validateLoginCredentials(credentials: Credentials): Future[Option[Customer]]  = {
    credentials match {
      case Credentials.Provided(token) => {
         val x = validateToken(token, AppConfig.getString("jwt.secret"))
        x match {
          case Right(value) => dbService.findCustomerById(value).map{
            case Some(customer) => Some(customer)
            case None => None
          }
          case Left(_) => Future.successful(None)
        }
      }
    }
  }

}