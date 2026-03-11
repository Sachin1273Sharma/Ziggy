package com.ziggy.database.schema

import com.ziggy.database.model.{Partner, PartnerVehicle}
import slick.ast.BaseTypedType
import slick.jdbc.JdbcType
import slick.jdbc.PostgresProfile.api.*

import java.time.Instant

class PartnerSchema(tag: Tag) extends Table[Partner](tag, "partners") {
  import PartnerSchema.given



  def id = column[String]("id", O.PrimaryKey)
  def name = column[String]("name")
  def email = column[String]("email")
  def phoneNumber = column[String]("phone_number")
  def vehicle = column[PartnerVehicle]("vehicle")
  def city = column[String]("city")
  def isOpenToService = column[Boolean]("is_open_to_service")
  def isAvailable = column[Boolean]("is_available")
  def isEngagedInOrder = column[Boolean]("is_engaged_in_order")
  def currentOrderId = column[Option[String]]("current_order_id")
  def createdAt = column[Option[Instant]]("created_at")
  def updatedAt = column[Option[Instant]]("updated_at")
	def serviceablePins = column[List[String]]("pin_codes")

  def emailIndex = index("idx_partners_email", email, unique = true)
  def phoneIndex = index("idx_partners_phone_number", phoneNumber, unique = true)
  def availabilityIndex = index("idx_partners_is_available", isAvailable)

  def * = (
    id.?,
    name,
    email,
    phoneNumber,
    vehicle,
    city,
    isOpenToService,
    isAvailable,
    isEngagedInOrder,
    currentOrderId,
    createdAt,
    updatedAt,
	  serviceablePins
  ).mapTo[Partner]
}

object PartnerSchema {
  given BaseColumnType[PartnerVehicle] = MappedColumnType.base[PartnerVehicle, String](
    _.toString.toLowerCase,
    {
      case "cycle" => PartnerVehicle.Cycle
      case "bike" => PartnerVehicle.Bike
      case other => throw new IllegalArgumentException(s"Unsupported partner vehicle: $other")
    }
  )

	given pincodeMapper: JdbcType[List[String]] &
		BaseTypedType[List[String]] = MappedColumnType.base[List[String], String](
		list => list.mkString(","),
		str => str.split(",").toList
	)
  val partners = TableQuery[PartnerSchema]
}
