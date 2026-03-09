package com.ziggy.database.schema

import com.ziggy.database.model.Restaurant
import slick.jdbc.PostgresProfile.api.*

import java.sql.Time
import java.sql.Timestamp

class ResturantScheme(tag: Tag) extends Table[Restaurant](tag, "restaurants") {

  def id = column[String]("id", O.PrimaryKey)
  def addressId = column[Option[String]]("address_id")
  def joiningDate = column[Timestamp]("joining_date")
  def latitude = column[Double]("latitude")
  def longitude = column[Double]("longitude")
  def openingTime = column[Time]("opening_time")
  def closingTime = column[Time]("closing_time")
  def isOpen = column[Boolean]("is_open")

  def isOpenIndex = index("idx_restaurants_is_open", isOpen)
  def addressIdIndex = index("idx_restaurants_address_id", addressId)

  def addressFk = foreignKey("fk_restaurants_address_id", addressId, AddressSchema.addresses)(_.id.?)

  def * = (
    id.?,
    addressId,
    joiningDate,
    latitude,
    longitude,
    openingTime,
    closingTime,
    isOpen
  ).mapTo[Restaurant]
}

object ResturantScheme {
  val restaurants = TableQuery[ResturantScheme]
}
