package com.ziggy.service

import com.ziggy.database.model.{AddRestaurant, Address, Restaurant, UpdateRestaurant}
import com.ziggy.utils.Logger

import java.sql.Timestamp
import java.util.UUID.randomUUID
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

@Singleton
class RestaurantService @Inject()
															  (dbService : DbService)(using ec: ExecutionContext) extends Logger {



	def addRestaurant(data : AddRestaurant): Future[(Option[String], String)] = {
		val addressId = randomUUID.toString
		 val address = data.address.copy(id = Some(addressId))

		val restaurant = Restaurant(id = Some(randomUUID.toString),
		                            name = data.name,
		                            email = data.email,
																phone = data.phone,
																gstin = data.gstin,
																addressId = Some(addressId),
																joiningDate = Timestamp,
																openingTime = data.openingTime,
																closingTime = data.closingTime,
																pinCodes = data.pinCodes
		                            )
		dbService.addRestaurant(address,restaurant)
	}

	def updateRestaurant(data: UpdateRestaurant): Future[Boolean] = {
		dbService.findRestaurantWithAddressByRestaurantId(data.id).transformWith {
			case Success(value) => value match {
				case Some(restaurant, address) => {
					val newRestaurant = restaurant.copy(
						name = data.name.getOrElse(restaurant.name),
						email = data.email.getOrElse(restaurant.email),
						phone = data.phone.getOrElse(restaurant.phone),
						gstin = data.gstin.getOrElse(restaurant.gstin),
						openingTime = data.openingTime.getOrElse(restaurant.openingTime),
						closingTime = data.closingTime.getOrElse(restaurant.closingTime),
						isOpen = data.isOpen.getOrElse(restaurant.isOpen),
						pinCodes = data.pincodes.getOrElse(restaurant.pinCodes)
						)
					val newAddress = data.address.getOrElse(address)
					dbService.updateRestaurantWithAddress(restaurant, address)
				}
				case None => Future.successful(false)
			}
			case Failure(ex) => Future.failed(ex)
		}
	}
}