package com.ziggy.database.table

import com.ziggy.database.model.{Address, User, Order, OrderStatus, Restaurant}
import com.ziggy.database.schema.{AddressSchema, CustomerAddressSchema, UserSchema, OrderSchema, ResturantScheme}
import slick.jdbc.PostgresProfile.api.*

import java.util.UUID
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

final case class OrderRoutingContext(
	                                    order: Order,
	                                    customer: User,
	                                    customerAddress: Option[Address],
	                                    restaurant: Restaurant,
	                                    restaurantAddress: Option[Address]
)

final class OrderTable(db: Database)(implicit ec: ExecutionContext) {
  private val orders = OrderSchema.orders
  private val customers = UserSchema.customers
  private val customerAddresses = CustomerAddressSchema.customerAddresses
  private val addresses = AddressSchema.addresses
  private val restaurants = ResturantScheme.restaurants

  private def findAddressById(addressId: Option[String]): DBIO[Option[Address]] =
    addressId match {
      case Some(id) => addresses.filter(_.id === id).result.headOption
      case None => DBIO.successful(None)
    }

  private def findCurrentCustomerAddress(customerId: Option[UUID]): DBIO[Option[Address]] = {
	  customerId match {
		  case Some(id) =>
			  customerAddresses
				  .filter(link => link.customerId === id)
				  .map(_.addressId)
				  .result
				  .headOption
				  .flatMap(findAddressById)
		  case None => DBIO.successful(None)
	  }
  }


	  def assignPartnerAction(orderId: String, partnerId: String) : DBIO[Int] = {
		  orders.filter(_.id === orderId).
			  map(x => (x.partnerId, x.isPartnerAssigned)).
			  update((Some(partnerId), true))
	  }


	def createTable: Future[Unit] =
    db.run(orders.schema.create)

  def createTableIfNotExists: Future[Unit] =
    db.run(orders.schema.createIfNotExists)

  def dropTable: Future[Unit] =
    db.run(orders.schema.drop)

  def dropTableIfExists: Future[Unit] =
    db.run(orders.schema.dropIfExists)

  def insert(order: Order): Future[String] = {
    db.run(orders += order).map(_ => order.id.get).recover{
      case ex : Exception =>  throw ex
    }
  }

  def insertAll(values: Seq[Order]): Future[Option[Int]] =
    db.run(orders ++= values.map(order => order.copy(id = order.id.orElse(Some(UUID.randomUUID().toString)))))

  def findById(id: String): Future[Option[Order]] =
    db.run(orders.filter(_.id === id).result.headOption)

  def findByCustomerId(customerId: String): Future[Seq[Order]] =
    db.run(orders.filter(_.customerId === UUID.fromString(customerId)).sortBy(_.id.asc).result)

  def findByRestaurantId(restaurantId: String): Future[Seq[Order]] =
    db.run(orders.filter(_.restaurantId === restaurantId).sortBy(_.id.asc).result)

  def findByStatus(status: String): Future[Seq[Order]] =
    db.run(orders.filter(_.status === status).sortBy(_.id.asc).result)

  def listAll: Future[Seq[Order]] =
    db.run(orders.sortBy(_.id.asc).result)

  def findRoutingContext(orderId: String): Future[Option[OrderRoutingContext]] = {
    val action = orders
      .filter(_.id === orderId)
      .join(customers)
      .on(_.customerId === _.id)
      .join(restaurants)
      .on { case ((order, _), restaurant) => order.restaurantId === restaurant.id }
      .map { case ((order, customer), restaurant) => (order, customer, restaurant) }
      .result
      .headOption
      .flatMap {
        case Some((order, customer, restaurant)) =>
          for {
            customerAddress <- findCurrentCustomerAddress(customer.id)
            restaurantAddress <- findAddressById(restaurant.addressId)
          } yield Some(OrderRoutingContext(order, customer, customerAddress, restaurant, restaurantAddress))
        case None =>
          DBIO.successful(None)
      }

    db.run(action)
  }

  def update(id: String, order: Order): Future[Int] = {
    val updatedOrder = order.copy(id = Some(id))
    db.run(orders.filter(_.id === id).update(updatedOrder))
  }

  def updateStatus(id: String, status: String): Future[Int] =
    db.run(orders.filter(_.id === id).map(_.status).update(status))

  def assignPartner(id: String, partnerId: String): Future[Int] =
    db.run(
      orders
        .filter(_.id === id)
        .map(order => (order.isPartnerAssigned, order.partnerId))
        .update((true, Some(partnerId)))
    )

  def clearPartner(id: String): Future[Int] =
    db.run(
      orders
        .filter(_.id === id)
        .map(order => (order.isPartnerAssigned, order.partnerId))
        .update((false, None))
    )

  def delete(id: String): Future[Int] =
    db.run(orders.filter(_.id === id).delete)

  def deleteByCustomerId(customerId: String): Future[Int] =
    db.run(orders.filter(_.customerId === UUID.fromString(customerId)).delete)

  def deleteByRestaurantId(restaurantId: String): Future[Int] =
    db.run(orders.filter(_.restaurantId === restaurantId).delete)

  def deleteAll: Future[Int] =
    db.run(orders.delete)

	def cancelOrder(id : String) : DBIO[Int] =
		orders.filter(_.id === id).map(_.status).update(OrderStatus.Cancelled.toString)

	def orderDelivered(id : String) : DBIO[Int] =
		orders.filter(_.id === id).map(_.status).update(OrderStatus.Delivered.toString)
}
