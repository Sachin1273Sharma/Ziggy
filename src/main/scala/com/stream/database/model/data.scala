package com.stream.database.model

import java.time.Instant
import java.util.UUID

final case class Address(
                          line1: Option[String] = None,
                          line2: Option[String] = None,
                          city: Option[String] = None,
                          country: Option[String] = None,
                          pincode: Option[String] = None
                        )

final case class Customer(
                           id: Option[UUID] = None,
                           name: Option[String] = None,
                           email: Option[String] = None,
                           passwordHash: Option[String] = None,
                           phoneNumber: Option[String] = None,
                           address: Option[Address] = None,
                           isProMember: Option[Boolean] = None,
                           isActive: Option[Boolean] = None,
                           dateOfBirth: Option[String] = None,
                           notes: Option[String] = None,
                           createdAt: Option[Instant] = None,
                           updatedAt: Option[Instant] = None
                         )

final case class RegisterRequest(email: String, password: String, name: Option[String])
final case class LoginRequest(email: String, password: String)
final case class RefreshRequest(refreshToken: String)
final case class MessageResponse(message: String)


//Security
sealed trait Security

final case class JwtLoginCred(userId : String, iat : Long) extends Security


