package com.ziggy.database.table

import com.ziggy.database.model.Item
import com.ziggy.database.schema.ItemScheme
import slick.jdbc.PostgresProfile.api.*

import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import java.util.UUID

final class ItemTable(db: Database)(implicit ec: ExecutionContext) {
  private val items = ItemScheme.items

  def createTable: Future[Unit] =
    db.run(items.schema.create)

  def createTableIfNotExists: Future[Unit] =
    db.run(items.schema.createIfNotExists)

  def dropTable: Future[Unit] =
    db.run(items.schema.drop)

  def dropTableIfExists: Future[Unit] =
    db.run(items.schema.dropIfExists)

  def insert(item: Item): Future[String] = {
    val itemToInsert = item.copy(id = item.id.orElse(Some(UUID.randomUUID().toString)))
    db.run((items += itemToInsert).map(_ => itemToInsert.id.get))
  }

  def insertAll(values: Seq[Item]): Future[Option[Int]] =
    db.run(items ++= values.map(item => item.copy(id = item.id.orElse(Some(UUID.randomUUID().toString)))))

  def findById(id: String): Future[Option[Item]] =
    db.run(items.filter(_.id === id).result.headOption)

  def findByRestaurantId(restaurantId: String): Future[Seq[Item]] =
    db.run(items.filter(_.restaurantId === restaurantId).sortBy(_.id.asc).result)

  def listAll: Future[Seq[Item]] =
    db.run(items.sortBy(_.id.asc).result)

  def update(id: String, item: Item): Future[Int] = {
    val updatedItem = item.copy(id = Some(id))
    db.run(items.filter(_.id === id).update(updatedItem))
  }

  def updateAvailability(id: String, isAvailable: Boolean): Future[Int] =
    db.run(items.filter(_.id === id).map(_.isAvailable).update(isAvailable))

  def updateQuick(id: String, quick: Boolean): Future[Int] =
    db.run(items.filter(_.id === id).map(_.quick).update(quick))

  def updateQuantity(id: String, quantityLeft: Int): Future[Int] =
    db.run(items.filter(_.id === id).map(_.quantityLeft).update(quantityLeft))

  def delete(id: String): Future[Int] =
    db.run(items.filter(_.id === id).delete)

  def deleteByRestaurantId(restaurantId: String): Future[Int] =
    db.run(items.filter(_.restaurantId === restaurantId).delete)

  def deleteAll(): Future[Int] =
    db.run(items.delete)
}
