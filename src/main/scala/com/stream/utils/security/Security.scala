package com.stream.utils.security

import akka.http.scaladsl.server.directives.Credentials
import com.stream.utils.AppConfig
import com.stream.utils.jwt.Jwt
import io.circe.*
import com.stream.database.model.data


trait Security extends Jwt {

  /**
   * @param credentials Will provide token
   */
  def validateLoginCredentials(credentials: Credentials): Option[String]  = {
    credentials match {
      case Credentials.Provided(token) => {
        if (validateToken(token, AppConfig.getString(jwt.secret)))
          {
            
          }
      }
    }
  }

}