package com.ziggy.database.schema

import com.ziggy.database.model.Address
import slick.jdbc.PostgresProfile.api.*

class AddressSchema(tag: Tag) extends Table[Address](tag, "addresses") {

  def id = column[String]("id", O.PrimaryKey)
  def line1 = column[Option[String]]("line_1")
  def line2 = column[Option[String]]("line_2")
  def city = column[Option[String]]("city")
  def country = column[Option[String]]("country")
  def pincode = column[Option[String]]("pincode")
  def latitude = column[Option[Double]]("latitude")
  def longitude = column[Option[Double]]("longitude")

  def * = (
    id.?,
    line1,
    line2,
    city,
    country,
    pincode,
    latitude,
    longitude
  ).mapTo[Address]
}

object AddressSchema {
  val addresses = TableQuery[AddressSchema]
}
