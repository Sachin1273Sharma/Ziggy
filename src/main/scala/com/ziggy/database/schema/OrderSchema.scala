package com.ziggy.database.schema

import com.ziggy.database.model.Order
import slick.jdbc.PostgresProfile.api.*

import java.util.UUID

class OrderSchema(tag: Tag) extends Table[Order](tag, "orders") {

  def id = column[String]("id", O.PrimaryKey)
  def customerId = column[UUID]("customer_id")
  def restaurantId = column[String]("restaurant_id")
  def isPartnerAssigned = column[Boolean]("is_partner_assigned")
  def partnerId = column[Option[String]]("partner_id")
  def totalAmount = column[BigDecimal]("total_amount")
  def status = column[String]("status")

  def customerIdIndex = index("idx_orders_customer_id", customerId)
  def restaurantIdIndex = index("idx_orders_restaurant_id", restaurantId)
  def statusIndex = index("idx_orders_status", status)
  def partnerIdIndex = index("idx_orders_partner_id", partnerId)

  def customerFk = foreignKey("fk_orders_customer_id", customerId, CustomerSchema.customers)(_.id)

  def * = (
    id.?,
    customerId,
    restaurantId,
    isPartnerAssigned,
    partnerId,
    totalAmount,
    status
  ).mapTo[Order]
}

object OrderSchema {
  val orders = TableQuery[OrderSchema]
}
