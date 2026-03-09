package com.ziggy.database.table

import com.ziggy.database.model.{Partner, PartnerVehicle}
import com.ziggy.database.schema.PartnerSchema
import slick.jdbc.PostgresProfile.api.*

import java.time.Instant
import java.util.UUID
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

final class PartnerTable(db: Database)(implicit ec: ExecutionContext) {
  import PartnerSchema.given

  private val partners = PartnerSchema.partners

  def createTable: Future[Unit] =
    db.run(partners.schema.create)

  def createTableIfNotExists: Future[Unit] =
    db.run(partners.schema.createIfNotExists)

  def dropTable: Future[Unit] =
    db.run(partners.schema.drop)

  def dropTableIfExists: Future[Unit] =
    db.run(partners.schema.dropIfExists)

  def insert(partner: Partner): Future[String] = {
    val now = Instant.now()
    val partnerToInsert = partner.copy(
      id = partner.id.orElse(Some(UUID.randomUUID().toString)),
      createdAt = partner.createdAt.orElse(Some(now)),
      updatedAt = Some(now)
    )

    db.run((partners += partnerToInsert).map(_ => partnerToInsert.id.get))
  }

  def insertAll(values: Seq[Partner]): Future[Option[Int]] = {
    val now = Instant.now()
    db.run(
      partners ++= values.map { partner =>
        partner.copy(
          id = partner.id.orElse(Some(UUID.randomUUID().toString)),
          createdAt = partner.createdAt.orElse(Some(now)),
          updatedAt = Some(now)
        )
      }
    )
  }

  def findById(id: String): Future[Option[Partner]] =
    db.run(partners.filter(_.id === id).result.headOption)

  def findByEmail(email: String): Future[Option[Partner]] =
    db.run(partners.filter(_.email === email).result.headOption)

  def findByVehicle(vehicle: PartnerVehicle): Future[Seq[Partner]] =
    db.run(partners.filter(_.vehicle === vehicle).sortBy(_.id.asc).result)

  def listAll: Future[Seq[Partner]] =
    db.run(partners.sortBy(_.id.asc).result)

  def listAvailable: Future[Seq[Partner]] =
    db.run(
      partners
        .filter(partner =>
          partner.isOpenToService && partner.isAvailable && (partner.isEngagedInOrder === false)
        )
        .sortBy(_.id.asc)
        .result
    )

  def update(id: String, partner: Partner): Future[Int] = {
    val updatedPartner = partner.copy(
      id = Some(id),
      updatedAt = Some(Instant.now())
    )

    db.run(partners.filter(_.id === id).update(updatedPartner))
  }

  def updateAvailability(id: String, isAvailable: Boolean): Future[Int] =
    db.run(
      partners
        .filter(_.id === id)
        .map(partner => (partner.isAvailable, partner.updatedAt))
        .update((isAvailable, Some(Instant.now())))
    )

  def updateOpenToService(id: String, isOpenToService: Boolean): Future[Int] =
    db.run(
      partners
        .filter(_.id === id)
        .map(partner => (partner.isOpenToService, partner.updatedAt))
        .update((isOpenToService, Some(Instant.now())))
    )

  def assignOrder(id: String, orderId: String): Future[Int] =
    db.run(
      partners
        .filter(_.id === id)
        .map(partner => (partner.isAvailable, partner.isEngagedInOrder, partner.currentOrderId, partner.updatedAt))
        .update((false, true, Some(orderId), Some(Instant.now())))
    )

  def clearOrder(id: String): Future[Int] =
    db.run(
      partners
        .filter(_.id === id)
        .map(partner => (partner.isAvailable, partner.isEngagedInOrder, partner.currentOrderId, partner.updatedAt))
        .update((true, false, None, Some(Instant.now())))
    )

  def delete(id: String): Future[Int] =
    db.run(partners.filter(_.id === id).delete)

  def deleteAll: Future[Int] =
    db.run(partners.delete)
}
