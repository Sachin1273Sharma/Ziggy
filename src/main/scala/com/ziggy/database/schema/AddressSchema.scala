package com.ziggy.database.schema

import com.ziggy.database.model.Address
import slick.jdbc.PostgresProfile.api.*

class AddressSchema(tag: Tag) extends Table[Address](tag, "addresses") {

  def id = column[String]("id", O.PrimaryKey)
  def line1 = column[Option[String]]("line_1")
  def line2 = column[String]("line_2")
  def city = column[String]("city")
  def country = column[String]("country")
  def pincode = column[String]("pincode")
  def latitude = column[Double]("latitude")
  def longitude = column[Double]("longitude")

  def * = (
    id,
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
