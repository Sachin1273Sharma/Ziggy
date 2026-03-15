package com.ziggy.service

import com.ziggy.database.model.{Address, Order, Partner, Restaurant, User}
import com.ziggy.database.table.{AddressTable, CustomerTable, OrderRoutingContext, OrderTable, PartnerTable, RestaurantTable}
import slick.dbio.DBIO
import slick.jdbc.PostgresProfile.api.*

import java.util.UUID
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton class DbService @Inject(
	                                  customerTable: CustomerTable,
	                                  orderTable: OrderTable,
	                                  partnerTable: PartnerTable,
	                                  addressTable : AddressTable,
	                                  restaurantTable : RestaurantTable,
	                                  db: Database
                                  )(using ec: ExecutionContext) {

	def findUserByEmail(email: String): Future[Option[User]] = {
		customerTable.findByEmail(email)
	}

	def findUserById(id: String): Future[Option[User]] = {
		customerTable.findById(id)
	}

	def register(data: User): Future[UUID] = {
		customerTable.insert(data)
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

	def assignPartnerOrder(id: String, orderId: String): Future[Int] = {
		partnerTable.assignOrder(id, orderId)
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

	/* order */
	def findOrderAndRestaurantAddressByOrderId(orderId: String): Future[Option[OrderRoutingContext]] = {
		orderTable.findRoutingContext(orderId)
	}

	/*Restaurant */
	def findRestaurantWithAddressByRestaurantId(restaurantId: String): Future[Option[(Restaurant, Address)]] = {
		restaurantTable.findRestaurantWithAddress(restaurantId)
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

	def updateRestaurantWithAddress(restaurant: Restaurant, address: Address): Future[String] = {
		db.run((for {
			isAddressUpdated <- addressTable.update(address.id.getOrElse(""), address)
			isRestaurantUpdated <- restaurantTable.update(restaurant.id.getOrElse(""), restaurant)
		} yield {
			if ((isAddressUpdated + isRestaurantUpdated) >= 1) {
				"UPDATED"
			} else {
				"NOT UPDATED"
			}
		}).transactionally)
	}


}
