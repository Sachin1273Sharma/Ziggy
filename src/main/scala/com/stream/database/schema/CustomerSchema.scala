package com.stream.database.schema

import com.stream.database.model.Address
import com.stream.database.model.Customer

import java.time.Instant
import java.util.UUID
import slick.jdbc.PostgresProfile.api.*

class CustomerSchema(tag: Tag) extends Table[Customer](tag, "customers") {
  def id = column[UUID]("id", O.PrimaryKey)
  def name = column[Option[String]]("name")
  def email = column[Option[String]]("email")
  def passwordHash = column[Option[String]]("password_hash")
  def phoneNumber = column[Option[String]]("phone_number")

  def addressLine1 = column[Option[String]]("address_line_1")
  def addressLine2 = column[Option[String]]("address_line_2")
  def city = column[Option[String]]("city")
  def country = column[Option[String]]("country")
  def pincode = column[Option[String]]("pincode")

  def isProMember = column[Option[Boolean]]("is_pro_member")
  def isActive = column[Option[Boolean]]("is_active")
  def dateOfBirth = column[Option[String]]("date_of_birth")
  def notes = column[Option[String]]("notes")
  def createdAt = column[Option[Instant]]("created_at")
  def updatedAt = column[Option[Instant]]("updated_at")

  def emailIndex = index("idx_customers_email", email, unique = true)

  private def toAddress(
    line1: Option[String],
    line2: Option[String],
    cityValue: Option[String],
    countryValue: Option[String],
    pincodeValue: Option[String]
  ): Option[Address] = {
    if (List(line1, line2, cityValue, countryValue, pincodeValue).forall(_.isEmpty)) None
    else Some(Address(line1, line2, cityValue, countryValue, pincodeValue))
  }

  private def fromAddress(address: Option[Address]): (Option[String], Option[String], Option[String], Option[String], Option[String]) = {
    val value = address.getOrElse(Address())
    (value.line1, value.line2, value.city, value.country, value.pincode)
  }

  def * = (
    id.?,
    name,
    email,
    passwordHash,
    phoneNumber,
    addressLine1,
    addressLine2,
    city,
    country,
    pincode,
    isProMember,
    isActive,
    dateOfBirth,
    notes,
    createdAt,
    updatedAt
  ).shaped <> (
    {
      case (
          idValue,
          nameValue,
          emailValue,
          passwordHashValue,
          phoneNumberValue,
          addressLine1Value,
          addressLine2Value,
          cityValue,
          countryValue,
          pincodeValue,
          isProMemberValue,
          isActiveValue,
          dateOfBirthValue,
          notesValue,
          createdAtValue,
          updatedAtValue
          ) =>
        Customer(
          id = idValue,
          name = nameValue,
          email = emailValue,
          passwordHash = passwordHashValue,
          phoneNumber = phoneNumberValue,
          address = toAddress(addressLine1Value, addressLine2Value, cityValue, countryValue, pincodeValue),
          isProMember = isProMemberValue,
          isActive = isActiveValue,
          dateOfBirth = dateOfBirthValue,
          notes = notesValue,
          createdAt = createdAtValue,
          updatedAt = updatedAtValue
        )
    },
    (customer: Customer) => {
      val (line1, line2, cityValue, countryValue, pincodeValue) = fromAddress(customer.address)
      Some((
        customer.id,
        customer.name,
        customer.email,
        customer.passwordHash,
        customer.phoneNumber,
        line1,
        line2,
        cityValue,
        countryValue,
        pincodeValue,
        customer.isProMember,
        customer.isActive,
        customer.dateOfBirth,
        customer.notes,
        customer.createdAt,
        customer.updatedAt
      ))
    }
  )
}

object CustomerSchema {
  val customers = TableQuery[CustomerSchema]
}
