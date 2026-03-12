package com.ziggy.service

import com.ziggy.database.model.{Customer, Order, Partner}
import com.ziggy.database.table.{CustomerTable, OrderRoutingContext, OrderTable, PartnerTable}
import slick.dbio.DBIO
import slick.jdbc.PostgresProfile.api.*

import java.util.UUID
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton class DbService @Inject(
	                                  customerTable: CustomerTable,
	                                  orderTable: OrderTable,
	                                  partnerTable: PartnerTable,
	                                  db: Database
                                  )(using ec: ExecutionContext) {

	def findCustomerByEmail(email: String): Future[Option[Customer]] = {
		customerTable.findByEmail(email)
	}

	def findCustomerById(id: String): Future[Option[Customer]] = {
		customerTable.findById(id)
	}

	def register(data: Customer): Future[UUID] = {
		customerTable.insert(data)
	}


	/* Order */

	def createOrder(order: Order): Future[String] = {
		orderTable.insert(order)
	}
	def findOrderById(orderId : String) : Future[Option[Order]] = {
		orderTable.findById(orderId)
	}
	def cancelOrder(orderId : String): Future[Int] = {
		db.run(orderTable.cancelOrder(orderId))
	}
	/* Partner */

	def createPartner(partner: Partner): Future[String] = {
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

	/* Transactional Queries */
	def assignPartner(partnerId: String, orderId: String): Future[Boolean] = {
		db.run((for {
			partnerAssigned <- partnerTable.assignOrderAction(partnerId,orderId)
			orderAssigned <- orderTable.assignPartnerAction(orderId, partnerId)} yield {
			if((partnerAssigned + orderAssigned) == 2)
				{
					true
				} else false
		}).transactionally)
	}

	def orderDelivered(orderId: String,partnerId : String): Future[Boolean] = {
		db.run((for {
			cancelOrder <- orderTable.cancelOrder(orderId)
			updatePartnerStatus <- partnerTable.freePartner(partnerId)
		} yield {
			if ((cancelOrder + updatePartnerStatus == 2))
			{
				true
			} else false
		}).transactionally)
	}

}
