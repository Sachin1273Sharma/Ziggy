package com.ziggy.database.table

import com.ziggy.database.model.CustomerAddress
import com.ziggy.database.schema.CustomerAddressSchema
import slick.jdbc.PostgresProfile.api.*

import java.util.UUID
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

final class CustomerAddressTable(db: Database)(implicit ec: ExecutionContext) {
  private val customerAddresses = CustomerAddressSchema.customerAddresses

  def createTable: Future[Unit] =
    db.run(customerAddresses.schema.create)

  def createTableIfNotExists: Future[Unit] =
    db.run(customerAddresses.schema.createIfNotExists)

  def dropTable: Future[Unit] =
    db.run(customerAddresses.schema.drop)

  def dropTableIfExists: Future[Unit] =
    db.run(customerAddresses.schema.dropIfExists)

  def insert(customerAddress: CustomerAddress): Future[String] = {
    val valueToInsert = customerAddress.copy(id = customerAddress.id.orElse(Some(UUID.randomUUID().toString)))
    db.run((customerAddresses += valueToInsert).map(_ => valueToInsert.id.get))
  }

  def insertAll(values: Seq[CustomerAddress]): Future[Option[Int]] =
    db.run(customerAddresses ++= values.map(value => value.copy(id = value.id.orElse(Some(UUID.randomUUID().toString)))))

  def findById(id: String): Future[Option[CustomerAddress]] =
    db.run(customerAddresses.filter(_.id === id).result.headOption)

  def findByCustomerId(customerId: String): Future[Seq[CustomerAddress]] =
    db.run(customerAddresses.filter(_.customerId === UUID.fromString(customerId)).sortBy(_.id.asc).result)

  def findCurrentDeliveryAddress(customerId: String): Future[Option[CustomerAddress]] =
    db.run(
      customerAddresses
        .filter(link => link.customerId === UUID.fromString(customerId) && link.isCurrentDelivery === true)
        .result
        .headOption
    )

  def listAll: Future[Seq[CustomerAddress]] =
    db.run(customerAddresses.sortBy(_.id.asc).result)

  def update(id: String, customerAddress: CustomerAddress): Future[Int] = {
    val updatedValue = customerAddress.copy(id = Some(id))
    db.run(customerAddresses.filter(_.id === id).update(updatedValue))
  }

  def setCurrentDeliveryAddress(customerId: String, addressId: String): Future[Int] = {
    val customerUuid = UUID.fromString(customerId)
    val clearExisting = customerAddresses
      .filter(link => link.customerId === customerUuid && link.isCurrentDelivery === true)
      .map(_.isCurrentDelivery)
      .update(false)

    val markCurrent = customerAddresses
      .filter(link => link.customerId === customerUuid && link.addressId === addressId)
      .map(_.isCurrentDelivery)
      .update(true)

    db.run((clearExisting >> markCurrent).transactionally)
  }

  def clearCurrentDeliveryAddress(customerId: String): Future[Int] =
    db.run(
      customerAddresses
        .filter(link => link.customerId === UUID.fromString(customerId) && link.isCurrentDelivery === true)
        .map(_.isCurrentDelivery)
        .update(false)
    )

  def delete(id: String): Future[Int] =
    db.run(customerAddresses.filter(_.id === id).delete)

  def deleteByCustomerId(customerId: String): Future[Int] =
    db.run(customerAddresses.filter(_.customerId === UUID.fromString(customerId)).delete)

  def deleteByAddressId(addressId: String): Future[Int] =
    db.run(customerAddresses.filter(_.addressId === addressId).delete)

  def deleteAll: Future[Int] =
    db.run(customerAddresses.delete)
}
