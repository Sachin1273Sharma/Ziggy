package com.ziggy.database.table

import com.ziggy.database.model.Order
import com.ziggy.database.schema.OrderSchema
import slick.jdbc.PostgresProfile.api.*

import java.util.UUID
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

final class OrderTable(db: Database)(implicit ec: ExecutionContext) {
  private val orders = OrderSchema.orders

  def createTable: Future[Unit] =
    db.run(orders.schema.create)

  def createTableIfNotExists: Future[Unit] =
    db.run(orders.schema.createIfNotExists)

  def dropTable: Future[Unit] =
    db.run(orders.schema.drop)

  def dropTableIfExists: Future[Unit] =
    db.run(orders.schema.dropIfExists)

  def insert(order: Order): Future[String] = {
    db.run(orders += order).map(_ => order.id.get).recover{
      case ex : Exception =>  throw ex
    }
  }

  def insertAll(values: Seq[Order]): Future[Option[Int]] =
    db.run(orders ++= values.map(order => order.copy(id = order.id.orElse(Some(UUID.randomUUID().toString)))))

  def findById(id: String): Future[Option[Order]] =
    db.run(orders.filter(_.id === id).result.headOption)

  def findByCustomerId(customerId: String): Future[Seq[Order]] =
    db.run(orders.filter(_.customerId === UUID.fromString(customerId)).sortBy(_.id.asc).result)

  def findByRestaurantId(restaurantId: String): Future[Seq[Order]] =
    db.run(orders.filter(_.restaurantId === restaurantId).sortBy(_.id.asc).result)

  def findByStatus(status: String): Future[Seq[Order]] =
    db.run(orders.filter(_.status === status).sortBy(_.id.asc).result)

  def listAll: Future[Seq[Order]] =
    db.run(orders.sortBy(_.id.asc).result)

  def update(id: String, order: Order): Future[Int] = {
    val updatedOrder = order.copy(id = Some(id))
    db.run(orders.filter(_.id === id).update(updatedOrder))
  }

  def updateStatus(id: String, status: String): Future[Int] =
    db.run(orders.filter(_.id === id).map(_.status).update(status))

  def assignPartner(id: String, partnerId: String): Future[Int] =
    db.run(
      orders
        .filter(_.id === id)
        .map(order => (order.isPartnerAssigned, order.partnerId))
        .update((true, Some(partnerId)))
    )

  def clearPartner(id: String): Future[Int] =
    db.run(
      orders
        .filter(_.id === id)
        .map(order => (order.isPartnerAssigned, order.partnerId))
        .update((false, None))
    )

  def delete(id: String): Future[Int] =
    db.run(orders.filter(_.id === id).delete)

  def deleteByCustomerId(customerId: String): Future[Int] =
    db.run(orders.filter(_.customerId === UUID.fromString(customerId)).delete)

  def deleteByRestaurantId(restaurantId: String): Future[Int] =
    db.run(orders.filter(_.restaurantId === restaurantId).delete)

  def deleteAll: Future[Int] =
    db.run(orders.delete)
}
