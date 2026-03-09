package com.ziggy.database.schema

import com.ziggy.database.model.CustomerAddress
import slick.jdbc.PostgresProfile.api.*

import java.util.UUID

class CustomerAddressSchema(tag: Tag) extends Table[CustomerAddress](tag, "customer_addresses") {

  def id = column[String]("id", O.PrimaryKey)
  def customerId = column[UUID]("customer_id")
  def addressId = column[String]("address_id")
  def isCurrentDelivery = column[Boolean]("is_current_delivery")

  def customerIdIndex = index("idx_customer_addresses_customer_id", customerId)
  def addressIdIndex = index("idx_customer_addresses_address_id", addressId)
  def currentDeliveryIndex = index("idx_customer_addresses_current_delivery", (customerId, isCurrentDelivery))

  def customerFk = foreignKey("fk_customer_addresses_customer_id", customerId, CustomerSchema.customers)(_.id)
  def addressFk = foreignKey("fk_customer_addresses_address_id", addressId, AddressSchema.addresses)(_.id)

  def * = (
    id.?,
    customerId,
    addressId,
    isCurrentDelivery
  ).mapTo[CustomerAddress]
}

object CustomerAddressSchema {
  val customerAddresses = TableQuery[CustomerAddressSchema]
}
