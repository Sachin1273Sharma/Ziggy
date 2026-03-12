package com.ziggy.actor
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import com.ziggy.service.{CancelOrder, Deliver, Delivered, DeliveryCommand, FindPartner, OrderConfirmed, PartnerAssigned, PartnerService, RetrySearchingPartner, doNothing}
import com.ziggy.utils.Logger

import scala.concurrent.duration.DurationInt
import scala.util.Success

object Delivery extends Logger{
  def apply(partnerService : PartnerService) : Behavior[DeliveryCommand] = {
	  Behaviors.withTimers {
		  timers =>
		  Behaviors.receive {
			  (context, message) => {
				  message match {
					  case Deliver(item, customerRef) => {
						  context.log.info(s"Delivering item $item")
						  customerRef ! OrderConfirmed(item)
						  Behaviors.same
					  }
					  case FindPartner(orderId,attempt) => {
						  val result = partnerService.checkRestaurantAndAssignPartner(orderId)
								if(attempt <= 3) {
									context.pipeToSelf(result) {
										case Success(true) => log.info("Partner Assigned")
																	doNothing
										case Success(false) => {
											if(attempt < 3) {
												RetrySearchingPartner(orderId, attempt)
											} else CancelOrder(orderId)
										}
									}
								}
						  Behaviors.same
					  }
					  case RetrySearchingPartner(orderId,attempt) => {
						      timers.startSingleTimer(key = orderId,FindPartner(orderId,attempt + 1),60.seconds)
						  Behaviors.same
					  }
					  case CancelOrder(orderId) => {
						  partnerService.cancelOrder(orderId)
						  Behaviors.same
					  }
					  case Delivered(orderId) => {

					  }
					  case doNothing =>
						  Behaviors.same
					  case _ => Behaviors.same
				  }
			  }
		  }
	  }
  }
}