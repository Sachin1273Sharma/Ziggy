package com.ziggy.service

import com.ziggy.database.model.Partner
import com.ziggy.utils.Logger

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

@Singleton class PartnerService @Inject()(dbService: DbService)(using ec: ExecutionContext) extends Logger {

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

	private def assignPartner(pincode: String, orderId: String) :Future[Boolean] = {
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

}