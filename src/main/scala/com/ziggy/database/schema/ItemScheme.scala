package com.ziggy.database.schema

import com.ziggy.database.model.Item
import slick.jdbc.PostgresProfile.api.*

import java.sql.Timestamp

class ItemScheme(tag: Tag) extends Table[Item](tag, "items") {

  def id = column[String]("id", O.PrimaryKey)
  def restaurantId = column[String]("restaurant_id")
  def name = column[String]("name")
  def price = column[Double]("price")
  def rating = column[Double]("rating")
  def isAvailable = column[Boolean]("is_available")
  def quick = column[Boolean]("quick")
  def quantityLeft = column[Int]("quantity_left")
  def createdAt = column[Timestamp]("created_at")
  def updatedAt = column[Option[Timestamp]]("updated_at")

  def restaurantFk = foreignKey("fk_items_restaurant_id", restaurantId, ResturantScheme.restaurants)(_.id, onDelete = ForeignKeyAction.Cascade)

  def * = (
    id.?,
    restaurantId,
    name,
    price,
    rating,
    isAvailable,
    quick,
    quantityLeft,
    createdAt,
    updatedAt
  ).mapTo[Item]
}

object ItemScheme {
  val items = TableQuery[ItemScheme]
}
