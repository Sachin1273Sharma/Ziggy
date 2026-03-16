package com.ziggy.service

import com.ziggy.database.model.{Address, Item, Order, Partner, Restaurant, User}
import com.ziggy.database.table.{AddressTable, ItemTable, UserTable, OrderRoutingContext, OrderTable, PartnerTable, RestaurantTable}
import slick.dbio.DBIO
import slick.jdbc.PostgresProfile.api.*

import java.util.UUID
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DbService @Inject(
	userTable: UserTable,
	orderTable: OrderTable,
	partnerTable: PartnerTable,
	addressTable: AddressTable,
	restaurantTable: RestaurantTable,
	itemTable: ItemTable,
	db: Database
)(using ec: ExecutionContext) {

	def findUserByEmail(email: String): Future[Option[User]] = {
		userTable.findByEmail(email)
	}

	def findUserById(id: String): Future[Option[User]] = {
		userTable.findById(id)
	}

	def register(data: User): Future[String] = {
		userTable.insert(data)
	}


	/* Order */

	def createOrder(order: Order): Future[String] = {
		orderTable.insert(order)
	}

	def findOrderById(orderId: String): Future[Option[Order]] = {
		orderTable.findById(orderId)
	}

	def cancelOrder(orderId: String): Future[Int] = {
		db.run(orderTable.cancelOrder(orderId))
	}
	/* Partner */

	def addPartner(partner: Partner): Future[String] = {
		partnerTable.insert(partner)
	}

	def findPartnerById(id: String): Future[Option[Partner]] = {
		partnerTable.findById(id)
	}

	def findPartnerByEmail(email: String): Future[Option[Partner]] = {
		partnerTable.findByEmail(email)
	}

	def listPartners(): Future[Seq[Partner]] = {
		partnerTable.listAll
	}

	def listAvailablePartners(): Future[Seq[Partner]] = {
		partnerTable.listAvailable
	}

	def updatePartner(id: String, partner: Partner): Future[Int] = {
		partnerTable.update(id, partner)
	}

	def assignPartnerOrder(id: String, orderId: String): DBIO[Int] = {
		partnerTable.assignOrderAction(id, orderId)
	}

	def clearPartnerOrder(id: String): Future[Int] = {
		partnerTable.clearOrder(id)
	}

	def deletePartner(id: String): Future[Int] = {
		partnerTable.delete(id)
	}

	def findNearbyAvailablePartners(pincode: String): Future[Seq[Partner]] = {
		partnerTable.findAvailablePartners(pincode)
	}
	def findPartnerWithUserByRefId(refId : String): Future[Option[(Partner, User)]] = {
		partnerTable.findPartnerWithUserByRefId(refId)
	}

	/* order */
	def findOrderAndRestaurantAddressByOrderId(orderId: String): Future[Option[OrderRoutingContext]] = {
		orderTable.findRoutingContext(orderId)
	}

	/*Restaurant */
	def findRestaurantWithAddressByRestaurantId(restaurantId: String): Future[Option[(Restaurant, Address)]] = {
		restaurantTable.findRestaurantWithAddress(restaurantId)
	}

	def addItem(item: Item): Future[String] = {
		itemTable.insert(item)
	}

	def findItemByRestaurantIdAndItemId(restaurantId: String, itemId: String): Future[Option[Item]] = {
		itemTable.findByRestaurantIdAndItemId(restaurantId, itemId)
	}

	def updateItem(itemId: String, item: Item): Future[Int] = {
		itemTable.update(itemId, item)
	}

	/* Transactional Queries */
	def assignPartner(partnerId: String, orderId: String): Future[Boolean] = {
		db.run((for {
			partnerAssigned <- partnerTable.assignOrderAction(partnerId, orderId)
			orderAssigned <- orderTable.assignPartnerAction(orderId, partnerId)} yield {
			if ((partnerAssigned + orderAssigned) == 2) {
				true
			} else false
		}).transactionally)
	}

	def orderDelivered(orderId: String, partnerId: String): Future[Boolean] = {
		db.run((for {
			cancelOrder <- orderTable.cancelOrder(orderId)
			updatePartnerStatus <- partnerTable.freePartner(partnerId)
		} yield {
			if ((cancelOrder + updatePartnerStatus == 2)) {
				true
			} else false
		}).transactionally)
	}

	def addRestaurant(address: Address, restaurant: Restaurant): Future[(Option[String], String)]
	= {
		db.run((for {
			addressId <- addressTable.insert(address)
			restaurantId <- restaurantTable.insert(restaurant)
		} yield {
			(addressId, restaurantId)
		}).transactionally)
	}

	def updateRestaurantWithAddress(restaurant: Restaurant, address: Address): Future[Boolean] = {
		db.run((for {
			isAddressUpdated <- addressTable.update(address.id.getOrElse(""), address)
			isRestaurantUpdated <- restaurantTable.update(restaurant.id.getOrElse(""), restaurant)
		} yield {
			if ((isAddressUpdated + isRestaurantUpdated) >= 1) {
				true
			} else {
				false
			}
		}).transactionally)
	}

	def updatePartnerWithUser(
		partnerId: String,
		userId: String,
		partner: Partner,
		user: User
	): Future[Boolean] = {
		db.run((for {
			isPartnerUpdated <- partnerTable.updateAction(
			partnerId,
			partner
			)
			isUserUpdated <- userTable.updateAction(
			userId,
			user
		)} yield {
			if (isPartnerUpdated + isUserUpdated == 2) true else false
		}).transactionally
		       )
	}


}
