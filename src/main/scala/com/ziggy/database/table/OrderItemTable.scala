package com.ziggy.database.table

import com.ziggy.database.model.OrderItem
import com.ziggy.database.schema.OrderItemSchema
import slick.jdbc.PostgresProfile.api.*

import java.util.UUID
import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
@Singleton
final class OrderItemTable @Inject(db: Database)(implicit ec: ExecutionContext) {
  private val orderItems = OrderItemSchema.orderItems

  def createTable: Future[Unit] =
    db.run(orderItems.schema.create)

  def createTableIfNotExists: Future[Unit] =
    db.run(orderItems.schema.createIfNotExists)

  def dropTable: Future[Unit] =
    db.run(orderItems.schema.drop)

  def dropTableIfExists: Future[Unit] =
    db.run(orderItems.schema.dropIfExists)

  def insert(orderItem: OrderItem): Future[String] = {
    val orderItemToInsert = orderItem.copy(id = orderItem.id.orElse(Some(UUID.randomUUID().toString)))
    db.run((orderItems += orderItemToInsert).map(_ => orderItemToInsert.id.get))
  }

  def insertAll(values: Seq[OrderItem]): Future[Option[Int]] =
    db.run(orderItems ++= values.map(item => item.copy(id = item.id.orElse(Some(UUID.randomUUID().toString)))))

  def findById(id: String): Future[Option[OrderItem]] =
    db.run(orderItems.filter(_.id === id).result.headOption)

  def findByOrderId(orderId: String): Future[Seq[OrderItem]] =
    db.run(orderItems.filter(_.orderId === orderId).sortBy(_.id.asc).result)

  def findByItemId(itemId: String): Future[Seq[OrderItem]] =
    db.run(orderItems.filter(_.itemId === itemId).sortBy(_.id.asc).result)

  def listAll: Future[Seq[OrderItem]] =
    db.run(orderItems.sortBy(_.id.asc).result)

  def update(id: String, orderItem: OrderItem): Future[Int] = {
    val updatedOrderItem = orderItem.copy(id = Some(id))
    db.run(orderItems.filter(_.id === id).update(updatedOrderItem))
  }

  def updateQuantity(id: String, quantity: Int): Future[Int] =
    db.run(orderItems.filter(_.id === id).map(_.quantity).update(quantity))

  def delete(id: String): Future[Int] =
    db.run(orderItems.filter(_.id === id).delete)

  def deleteByOrderId(orderId: String): Future[Int] =
    db.run(orderItems.filter(_.orderId === orderId).delete)

  def deleteAll: Future[Int] =
    db.run(orderItems.delete)
}
