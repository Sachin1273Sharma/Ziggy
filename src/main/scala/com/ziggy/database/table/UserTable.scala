package com.ziggy.database.table

import com.ziggy.database.model.User
import com.ziggy.database.schema.UserSchema
import slick.jdbc.PostgresProfile.api.*

import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import java.util.UUID
import javax.inject.{Inject, Singleton}
@Singleton
final class UserTable @Inject(db: Database)
	(implicit ec: ExecutionContext) {
	private val users = UserSchema.users

	def createTable: Future[Unit] = {
		db.run(users.schema.create)
	}

	def createTableIfNotExists: Future[Unit] = {
		db.run(users.schema.createIfNotExists)
	}

	def dropTable: Future[Unit] = {
		db.run(users.schema.drop)
	}

	def dropTableIfExists: Future[Unit] = {
		db.run(users.schema.dropIfExists)
	}

	def insert(customer: User): Future[String] = {
		val customerToInsert = customer.copy(id = customer.id.orElse(Some(UUID.randomUUID().toString)))
		db.run((users += customerToInsert).map(_ => customerToInsert.id.get))
	}

	def insertAll(values: Seq[User]): Future[Option[Int]] = {
		db.run(users ++= values.map(value => value.copy(id = value.id.orElse(Some(UUID.randomUUID().toString)))))
	}

	def findById(id: String): Future[Option[User]] = {
		db.run(users.filter(_.id === id).result.headOption)
	}

	def findByEmail(email: String): Future[Option[User]] = {
		db.run(users.filter(_.email === Option(email)).result.headOption)
	}

	def listAll: Future[Seq[User]] = {
		db.run(users.sortBy(_.id.asc).result)
	}

	def update(
		id: String,
		customer: User
	): Future[Int] = {
		val updatedCustomer = customer.copy(id = Some(id))
		db.run(users.filter(_.id === id).update(updatedCustomer))
	}

	def updateAction(
		id: String,
		customer: User
	): DBIO[Int] = {
		val updatedCustomer = customer.copy(id = Some(id))
		users.filter(_.id === id).update(updatedCustomer)
	}

	def updateStatus(
		id: String,
		isActive: Option[Boolean]
	): Future[Int] = {
		db.run(users.filter(_.id === id).map(_.isActive).update(isActive))
	}

	def updateProMembership(
		id: String,
		isProMember: Option[Boolean]
	): Future[Int] = {
		db.run(users.filter(_.id === id).map(_.isProMember).update(isProMember))
	}

	def delete(id: String): Future[Int] = {
		db.run(users.filter(_.id === id).delete)
	}

	def deleteAll: Future[Int] = {
		db.run(users.delete)
	}
}
