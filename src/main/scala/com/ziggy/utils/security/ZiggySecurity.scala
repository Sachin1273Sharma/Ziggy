package com.ziggy.utils.security

import org.apache.pekko.http.scaladsl.model.headers.{HttpChallenge, HttpChallenges}
import org.apache.pekko.http.scaladsl.server.Directives.AuthenticationResult
import org.apache.pekko.http.scaladsl.server.directives.{AuthenticationResult, Credentials}
import com.ziggy.database.model.Customer
import com.ziggy.service.DbService
import com.ziggy.utils.AppConfig
import com.ziggy.utils.jwt.Jwt
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