package com.ziggy.database.table

import com.ziggy.database.model.Restaurant
import com.ziggy.database.schema.ResturantScheme
import slick.jdbc.PostgresProfile.api.*

import java.util.UUID
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

final class ResturantTable(db: Database)(implicit ec: ExecutionContext) {
  private val restaurants = ResturantScheme.restaurants

  def createTable: Future[Unit] =
    db.run(restaurants.schema.create)

  def createTableIfNotExists: Future[Unit] =
    db.run(restaurants.schema.createIfNotExists)

  def dropTable: Future[Unit] =
    db.run(restaurants.schema.drop)

  def dropTableIfExists: Future[Unit] =
    db.run(restaurants.schema.dropIfExists)

  def insert(resturant: Restaurant): Future[String] = {
    val resturantToInsert = resturant.copy(id = resturant.id.orElse(Some(UUID.randomUUID().toString)))
    db.run((restaurants += resturantToInsert).map(_ => resturantToInsert.id.get))
  }

  def insertAll(values: Seq[Restaurant]): Future[Option[Int]] =
    db.run(restaurants ++= values.map(resturant => resturant.copy(id = resturant.id.orElse(Some(UUID.randomUUID().toString)))))

  def findById(id: String): Future[Option[Restaurant]] =
    db.run(restaurants.filter(_.id === id).result.headOption)

  def findOpenRestaurants: Future[Seq[Restaurant]] =
    db.run(restaurants.filter(_.isOpen === true).sortBy(_.id.asc).result)

  def listAll: Future[Seq[Restaurant]] =
    db.run(restaurants.sortBy(_.id.asc).result)

  def update(id: String, resturant: Restaurant): Future[Int] = {
    val updatedResturant = resturant.copy(id = Some(id))
    db.run(restaurants.filter(_.id === id).update(updatedResturant))
  }

  def updateOpenStatus(id: String, isOpen: Boolean): Future[Int] =
    db.run(restaurants.filter(_.id === id).map(_.isOpen).update(isOpen))

  def delete(id: String): Future[Int] =
    db.run(restaurants.filter(_.id === id).delete)

  def deleteAll: Future[Int] =
    db.run(restaurants.delete)
}
