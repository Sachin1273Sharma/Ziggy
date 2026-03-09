package com.ziggy.database.table

import com.ziggy.database.model.Address
import com.ziggy.database.schema.AddressSchema
import slick.jdbc.PostgresProfile.api.*

import java.util.UUID
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

final class AddressTable(db: Database)(implicit ec: ExecutionContext) {
  private val addresses = AddressSchema.addresses

  def createTable: Future[Unit] =
    db.run(addresses.schema.create)

  def createTableIfNotExists: Future[Unit] =
    db.run(addresses.schema.createIfNotExists)

  def dropTable: Future[Unit] =
    db.run(addresses.schema.drop)

  def dropTableIfExists: Future[Unit] =
    db.run(addresses.schema.dropIfExists)

  def insert(address: Address): Future[String] = {
    val addressToInsert = address.copy(id = address.id.orElse(Some(UUID.randomUUID().toString)))
    db.run((addresses += addressToInsert).map(_ => addressToInsert.id.get))
  }

  def insertAll(values: Seq[Address]): Future[Option[Int]] =
    db.run(addresses ++= values.map(address => address.copy(id = address.id.orElse(Some(UUID.randomUUID().toString)))))

  def findById(id: String): Future[Option[Address]] =
    db.run(addresses.filter(_.id === id).result.headOption)

  def listAll: Future[Seq[Address]] =
    db.run(addresses.sortBy(_.id.asc).result)

  def update(id: String, address: Address): Future[Int] = {
    val updatedAddress = address.copy(id = Some(id))
    db.run(addresses.filter(_.id === id).update(updatedAddress))
  }

  def delete(id: String): Future[Int] =
    db.run(addresses.filter(_.id === id).delete)

  def deleteAll: Future[Int] =
    db.run(addresses.delete)
}
