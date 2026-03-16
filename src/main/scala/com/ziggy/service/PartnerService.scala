package com.ziggy.service

import com.ziggy.database.model.{AddPartner, Partner, PartnerVehicle, UpdatePartner,User}
import com.ziggy.utils.Logger

import java.time.Instant
import java.util.UUID.randomUUID
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

@Singleton class PartnerService @Inject()
																(dbService: DbService)
																(using ec: ExecutionContext) extends Logger {

	def checkRestaurantAndAssignPartner(orderId: String): Future[Boolean] = {
		for {
			data <- dbService.findOrderAndRestaurantAddressByOrderId(orderId)
			result <- data match {
				case Some(order, _, Some(custAdd), rest, Some(restAdd))
					if custAdd.city == restAdd.city && rest.pinCodes.contains(custAdd.pincode) =>
					assignPartner(custAdd.pincode, orderId)

				case _ =>
					Future.successful(false)
			}
		} yield result
	}

	private def assignPartner(pincode: String, orderId: String): Future[Boolean] = {
		for {
			partners <- dbService.findNearbyAvailablePartners(pincode)
			partner <- partners.headOption match {
				case Some(partnerToBeAssigned) => {
					val result = dbService.assignPartner(partnerToBeAssigned.id.getOrElse(""), orderId)
					result.transform { case Success(value) => Success(value)
					case Failure(ex) => {
						log.info(s"Exception occurred while assigning partner \n ${ex.getMessage}")
						Success(false)
					}
					}
				}
				case None => Future.successful(false)
			}
		} yield partner
	}

	def orderedDelivered(orderId: String): Future[Boolean] = {
		for {
			partnerId <- dbService.findOrderById(orderId) map {
				case Some(order) => order.partnerId
				case _ => None
			}
			resourceFreed <- if (partnerId.isDefined) {
				dbService.orderDelivered(orderId, partnerId.get)
			} else {
				Future.successful(false)
			}} yield (resourceFreed)
	}

	def addPartner(data: AddPartner): Future[String] = {
		val user = data.partner.get
		val partner = Partner(id = Some(randomUUID.toString),
		                      name = user.name.getOrElse(""),
		                      email = user.email.getOrElse(""), phoneNumber = user.phoneNumber
		                                                                          .getOrElse(""),
		                      vehicle = PartnerVehicle.valueOf(data.vehicle),
		                      pinCodes = data.pinCodes,
		                      createdAt = Some(Instant.now()),
		                      updatedAt = Some(Instant.now())
		                      )
		dbService.addPartner(partner)
	}

	def updatePartner(data: UpdatePartner): Future[Boolean] = {
		dbService.findPartnerWithUserByRefId(data.id) flatMap { case Some(
			partner,
			user
		) => {
			val newPartner = Partner(
				name = data.name.getOrElse(user.name.getOrElse("")),
				email = data.email.getOrElse(user.email.getOrElse("")),
				phoneNumber = data.phoneNumber.getOrElse(user.phoneNumber.getOrElse("")),
				isAvailable = data.isAvailable.getOrElse(partner.isAvailable),
				isOpenToService = data.isOpenToService.getOrElse(partner.isOpenToService),
				isEngagedInOrder = data.isEngagedInOrder.getOrElse(partner.isEngagedInOrder),
				pinCodes = data.pinCodes.getOrElse(partner.pinCodes),
				vehicle = (PartnerVehicle.valueOf(data.vehicle.getOrElse(partner.vehicle.toString))),
				currentOrderId = data.currentOrderId
				)
			val newUser = user.copy(
				name = data.name.orElse(user.name),
				email = data.email.orElse(user.email),
				phoneNumber = data.phoneNumber.orElse(user.phoneNumber)
				)

			val result = dbService.updatePartnerWithUser(
				partner.id.getOrElse(""),
				user.id.getOrElse(""),
				partner,
				user
				)
			result
		}
		case _ => Future.failed(new Exception("Partner data not found"))
		}
	}


}