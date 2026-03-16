package com.ziggy.service

import com.ziggy.database.model.{AddItem, AddItemEvent, AddRestaurant, Address, Item, Restaurant, UpdateItem, UpdateItemEvent, UpdateRestaurant}
import com.ziggy.utils.Logger

import java.time.Instant
import java.sql.Timestamp
import java.util.UUID.randomUUID
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

@Singleton
class RestaurantService @Inject()
															  (dbService : DbService)(using ec: ExecutionContext) extends Logger {

	private def currentTimestamp: Timestamp = Timestamp.from(Instant.now())

	def addRestaurant(data : AddRestaurant): Future[(Option[String], String)] = {
		val addressId = randomUUID.toString
		 val address = data.address.copy(id = Some(addressId))

		val restaurant = Restaurant(id = Some(randomUUID.toString),
		                            name = data.name,
		                            email = data.email,
																phone = data.phone,
																gstin = data.gstin,
																addressId = Some(addressId),
																joiningDate = currentTimestamp,
																openingTime = data.openingTime,
																closingTime = data.closingTime,
																isOpen = data.isOpen,
																pinCodes = data.pinCodes
		                            )
		dbService.addRestaurant(address,restaurant)
	}

	def updateRestaurant(data: UpdateRestaurant): Future[Boolean] = {
		dbService.findRestaurantWithAddressByRestaurantId(data.id).transformWith {
			case Success(value) => value match {
				case Some((restaurant, address)) => {
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
					val newAddress = data.address.map(_.copy(id = address.id)).getOrElse(address)
					dbService.updateRestaurantWithAddress(newRestaurant, newAddress)
				}
				case None => Future.successful(false)
			}
			case Failure(ex) => Future.failed(ex)
		}
	}

	def addItem(data: AddItemEvent): Future[String] = {
		dbService.findRestaurantWithAddressByRestaurantId(data.restaurantId).flatMap {
			case Some(_) =>
				val now = currentTimestamp
				val item = Item(
					id = Some(randomUUID.toString),
					restaurantId = data.restaurantId,
					name = data.item.name,
					price = data.item.price,
					rating = data.item.rating,
					isAvailable = data.item.isAvailable,
					quick = data.item.quick,
					quantityLeft = data.item.quantityLeft,
					createdAt = now,
					updatedAt = Some(now)
				)
				dbService.addItem(item)
			case None => Future.failed(new Exception(s"Restaurant ${data.restaurantId} not found"))
		}
	}

	def updateItem(data: UpdateItemEvent): Future[Boolean] = {
		dbService.findItemByRestaurantIdAndItemId(data.restaurantId, data.itemId).flatMap {
			case Some(existingItem) =>
				val updatedItem = mergeItemUpdate(existingItem, data.item)
				dbService.updateItem(data.itemId, updatedItem).map(_ > 0)
			case None => Future.successful(false)
		}
	}

	private def mergeItemUpdate(existingItem: Item, data: UpdateItem): Item = {
		existingItem.copy(
			name = data.name.getOrElse(existingItem.name),
			price = data.price.getOrElse(existingItem.price),
			rating = data.rating.getOrElse(existingItem.rating),
			isAvailable = data.isAvailable.getOrElse(existingItem.isAvailable),
			quick = data.quick.getOrElse(existingItem.quick),
			quantityLeft = data.quantityLeft.getOrElse(existingItem.quantityLeft),
			updatedAt = Some(currentTimestamp)
		)
	}
}
