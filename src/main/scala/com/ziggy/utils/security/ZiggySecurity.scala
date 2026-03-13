package com.ziggy.utils.security

import org.apache.pekko.http.scaladsl.model.headers.{HttpChallenge, HttpChallenges}
import org.apache.pekko.http.scaladsl.server.Directives.*
import org.apache.pekko.http.scaladsl.server.directives.{AuthenticationResult, Credentials}
import com.ziggy.database.model.User
import com.ziggy.service.DbService
import com.ziggy.utils.AppConfig
import com.ziggy.utils.jwt.Jwt
import io.circe.*
import org.apache.pekko.http.scaladsl.model.StatusCodes
import org.apache.pekko.http.scaladsl.server.AuthenticationFailedRejection.{CredentialsMissing, CredentialsRejected}
import org.apache.pekko.http.scaladsl.server.{AuthenticationFailedRejection, Directive1, RejectionError}

import javax.inject.*
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

@Singleton
class ZiggySecurity @Inject()(val dbService: DbService)(implicit ec : ExecutionContext) extends Jwt {

  /**
   * @param credentials Will provide token
   *
   */
  def validateLoginCredentials(credentials: Credentials): Future[Option[User]]  = {
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

	def authenticate: Directive1[User] = {
		optionalCookie("AUTH_TOKEN").flatMap {

			case Some(token) =>
				validateToken(token.value, AppConfig.getString("jwt.secret")) match {

					case Right(value) =>
						onSuccess(dbService.findCustomerById(value)).flatMap {

							case Some(customer) =>
								provide(customer)

							case None =>
								reject(AuthenticationFailedRejection(
									CredentialsRejected,
									HttpChallenge("auth-layer", "Invalid-token")
								))
						}

					case Left(_) =>
						reject(AuthenticationFailedRejection(
							CredentialsRejected,
							HttpChallenge("auth-layer", "Invalid-token")
						))
				}

			case _ =>
				reject(AuthenticationFailedRejection(
					CredentialsMissing,
					HttpChallenge("auth-layer", "Missing-token")
				))
		}
	}
}