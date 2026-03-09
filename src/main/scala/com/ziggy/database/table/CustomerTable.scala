package com.ziggy.database.table

import com.ziggy.database.model.Customer
import com.ziggy.database.schema.CustomerSchema
import slick.jdbc.PostgresProfile.api.*

import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import java.util.UUID

final class CustomerTable(db: Database)(implicit ec: ExecutionContext) {
  private val customers = CustomerSchema.customers

  def createTable: Future[Unit] =
    db.run(customers.schema.create)

  def createTableIfNotExists: Future[Unit] =
    db.run(customers.schema.createIfNotExists)

  def dropTable: Future[Unit] =
    db.run(customers.schema.drop)

  def dropTableIfExists: Future[Unit] =
    db.run(customers.schema.dropIfExists)

  def insert(customer: Customer): Future[UUID] = {
    val customerToInsert = customer.copy(id = customer.id.orElse(Some(UUID.randomUUID())))
    db.run((customers += customerToInsert).map(_ => customerToInsert.id.get))
  }

  def insertAll(values: Seq[Customer]): Future[Option[Int]] =
    db.run(customers ++= values.map(value => value.copy(id = value.id.orElse(Some(UUID.randomUUID())))))

  def findById(id: String): Future[Option[Customer]] =
    db.run(customers.filter(_.id === UUID.fromString(id)).result.headOption)

  def findByEmail(email: String): Future[Option[Customer]] =
    db.run(customers.filter(_.email === Option(email)).result.headOption)

  def listAll: Future[Seq[Customer]] =
    db.run(customers.sortBy(_.id.asc).result)

  def update(id: UUID, customer: Customer): Future[Int] = {
    val updatedCustomer = customer.copy(id = Some(id))
    db.run(customers.filter(_.id === id).update(updatedCustomer))
  }

  def updateStatus(id: UUID, isActive: Option[Boolean]): Future[Int] =
    db.run(customers.filter(_.id === id).map(_.isActive).update(isActive))

  def updateProMembership(id: UUID, isProMember: Option[Boolean]): Future[Int] =
    db.run(customers.filter(_.id === id).map(_.isProMember).update(isProMember))

  def delete(id: UUID): Future[Int] =
    db.run(customers.filter(_.id === id).delete)

  def deleteAll: Future[Int] =
    db.run(customers.delete)
}
