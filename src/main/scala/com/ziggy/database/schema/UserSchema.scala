	package com.ziggy.database.schema
	
	import com.ziggy.database.model.User
	
	import java.time.Instant
	import java.util.UUID
	import slick.jdbc.PostgresProfile.api.*
	
	class UserSchema(tag: Tag) extends Table[User](tag, "users") {
	def id = column[String]("id", O.PrimaryKey)
	def name = column[Option[String]]("name")
	def email = column[Option[String]]("email")
	def passwordHash = column[Option[String]]("password_hash")
	def phoneNumber = column[Option[String]]("phone_number")
	def isProMember = column[Option[Boolean]]("is_pro_member")
	def isActive = column[Option[Boolean]]("is_active")
	def dateOfBirth = column[Option[String]]("date_of_birth")
	def notes = column[Option[String]]("notes")
	def createdAt = column[Option[Instant]]("created_at")
	def updatedAt = column[Option[Instant]]("updated_at")
	def refId = column[Option[String]]("ref_id")
	
	def emailIndex = index("idx_customers_email", email, unique = true)
	
	def * = (
	id.?,
	name,
	email,
	passwordHash,
	phoneNumber,
	isProMember,
	isActive,
	dateOfBirth,
	notes,
	createdAt,
	updatedAt,
	refId
	).mapTo[User]
	}
	
	object UserSchema {
	val users = TableQuery[UserSchema]
	}
