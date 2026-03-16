package com.ziggy.database.schema

import com.ziggy.database.model.CustomerAddress
import slick.jdbc.PostgresProfile.api.*

import java.util.UUID

class CustomerAddressSchema(tag: Tag) extends Table[CustomerAddress](tag, "customer_addresses") {

  def id = column[String]("id", O.PrimaryKey)
  def customerId = column[String]("customer_id")
  def addressId = column[String]("address_id")

  def customerIdIndex = index("idx_customer_addresses_customer_id", customerId)
  def addressIdIndex = index("idx_customer_addresses_address_id", addressId)

  def customerFk = foreignKey("fk_customer_addresses_customer_id", customerId, UserSchema.users)(_.id)
  def addressFk = foreignKey("fk_customer_addresses_address_id", addressId, AddressSchema.addresses)(_.id)

  def * = (
    id.?,
    customerId,
    addressId
  ).mapTo[CustomerAddress]
}

object CustomerAddressSchema {
  val customerAddresses = TableQuery[CustomerAddressSchema]
}
