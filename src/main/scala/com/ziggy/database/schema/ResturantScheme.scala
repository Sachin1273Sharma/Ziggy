	package com.ziggy.database.schema

	import com.ziggy.database.model.Restaurant
	import slick.ast.BaseTypedType
	import slick.jdbc.JdbcType
	import slick.jdbc.PostgresProfile.api.*

	import java.sql.Time
	import java.sql.Timestamp

	class ResturantScheme(tag: Tag) extends Table[Restaurant](tag, "restaurants") {

	implicit val pincodeListMapper: JdbcType[List[String]] & BaseTypedType[List[String]] = MappedColumnType.base[List[String],String](
	list => list.mkString(","),
	str => str.split(",").toList
	)

	def id = column[String]("id", O.PrimaryKey)
	def name  = column[String]("name")
	def email = column[String]("email")
	def phone = column[String]("phone")
	def gstin = column[String]("gstin")
	def addressId = column[Option[String]]("address_id")
	def joiningDate = column[Timestamp]("joining_date")
	def openingTime = column[Time]("opening_time")
	def closingTime = column[Time]("closing_time")
	def isOpen = column[Boolean]("is_open")
	def serviceablePins = column[List[String]]("pin_codes")

	def isOpenIndex = index("idx_restaurants_is_open", isOpen)
	def addressIdIndex = index("idx_restaurants_address_id", addressId)

	def addressFk = foreignKey("fk_restaurants_address_id", addressId, AddressSchema.addresses)(_.id.?)


	def * = (
	id.?,
	name,
	email,
	phone,
	gstin,
	addressId,
	joiningDate,
	openingTime,
	closingTime,
	isOpen,
	serviceablePins
	).mapTo[Restaurant]
	}

	object ResturantScheme {
	val restaurants = TableQuery[ResturantScheme]
	}
