package com.ziggy.database.schema

import com.ziggy.database.model.OrderItem
import slick.jdbc.PostgresProfile.api.*

class OrderItemSchema(tag: Tag) extends Table[OrderItem](tag, "order_items") {

  def id = column[String]("id", O.PrimaryKey)
  def orderId = column[String]("order_id")
  def itemId = column[String]("item_id")
  def quantity = column[Int]("quantity")
  def priceAtTimeOfOrder = column[BigDecimal]("price_at_time_of_order")

  def orderIdIndex = index("idx_order_items_order_id", orderId)
  def itemIdIndex = index("idx_order_items_item_id", itemId)

  def orderFk = foreignKey("fk_order_items_order_id", orderId, OrderSchema.orders)(_.id, onDelete = ForeignKeyAction.Cascade)
  def itemFk = foreignKey("fk_order_items_item_id", itemId, ItemScheme.items)(_.id, onDelete = ForeignKeyAction.Restrict)

  def * = (
    id.?,
    orderId,
    itemId,
    quantity,
    priceAtTimeOfOrder
  ).mapTo[OrderItem]
}

object OrderItemSchema {
  val orderItems = TableQuery[OrderItemSchema]
}
