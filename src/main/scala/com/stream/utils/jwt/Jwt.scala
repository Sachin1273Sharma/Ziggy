package com.stream.utils.jwt

import com.stream.database.model.JwtLoginCred

import java.io.ObjectInputFilter.Config
import java.nio.charset.StandardCharsets
import java.util.Base64
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import io.circe.*
import io.circe.generic.auto.deriveDecoder
import io.circe.parser.*

class Jwt {
  private def base64Encode(input: String): String = {
    Base64.getUrlEncoder.withoutPadding().encodeToString(input.getBytes(StandardCharsets.UTF_8))
  }

  private def sign(data: String, secret: String): String = {
    val mac = Mac.getInstance("HmacSHA256")
    val secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256")
    mac.init(secretKey)
    val hmacBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8))
    Base64.getUrlEncoder.withoutPadding().encodeToString(hmacBytes)
  }

  def createToken(userId: String, secret: String): String = {
    val header = """{"alg":"HS256","typ":"JWT"}"""
//    val payload = s"""{"userId":"$userId","iat":${System.currentTimeMillis() / 1000}}"""
    val payload = JwtLoginCred(userId,System.currentTimeMillis()/1000)

    val encodedHeader = base64Encode(header)
    val encodedPayload = base64Encode(payload.toString)

    val dataToSign = s"$encodedHeader.$encodedPayload"
    val signature = sign(dataToSign, secret)

    s"$dataToSign.$signature"
  }

  def validateToken(token: String, secret: String): Either[Boolean,String] = {
    try {
      val parts = token.split(".")
      if (parts.length != 3) {
        return Left(false)
      }
      else {
        val dataToSign = parts(0) + parts(1)
        val sign = sign(dataToSign, secret)
        if (parts(2) != sign) {
          return Left(false)
        }
      }
      val loginCreds = (decode[JwtLoginCred] (parts(1)))
      loginCreds.map(cred => Right(cred))
      Left(false)
    }
    catch {
      case _ : Exception => Left(false)
    }
  }

}