package com.ziggy.database.table

import com.ziggy.database.model.CustomerAddress
import com.ziggy.database.schema.CustomerAddressSchema
import slick.jdbc.PostgresProfile.api.*

import java.util.UUID
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

final class CustomerAddressTable(db: Database)(implicit ec: ExecutionContext) {
  private val customerAddresses = CustomerAddressSchema.customerAddresses

  def insert(customerAddress: CustomerAddress): Future[String] = {
    val valueToInsert = customerAddress.copy(id = customerAddress.id.orElse(Some(UUID.randomUUID().toString)))
    db.run((customerAddresses += valueToInsert).map(_ => valueToInsert.id.get))
  }

  def insertAll(values: Seq[CustomerAddress]): Future[Option[Int]] =
    db.run(customerAddresses ++= values.map(value => value.copy(id = value.id.orElse(Some(UUID.randomUUID().toString)))))

  def findById(id: String): Future[Option[CustomerAddress]] =
    db.run(customerAddresses.filter(_.id === id).result.headOption)

  def findByCustomerId(customerId: String): Future[Seq[CustomerAddress]] =
    db.run(customerAddresses.filter(_.customerId === customerId).sortBy(_.id.asc).result)



  def listAll: Future[Seq[CustomerAddress]] =
    db.run(customerAddresses.sortBy(_.id.asc).result)

  def update(id: String, customerAddress: CustomerAddress): Future[Int] = {
    val updatedValue = customerAddress.copy(id = Some(id))
    db.run(customerAddresses.filter(_.id === id).update(updatedValue))
  }




  def delete(id: String): Future[Int] =
    db.run(customerAddresses.filter(_.id === id).delete)



  def deleteByAddressId(addressId: String): Future[Int] =
    db.run(customerAddresses.filter(_.addressId === addressId).delete)

  def deleteAll: Future[Int] =
    db.run(customerAddresses.delete)
}
