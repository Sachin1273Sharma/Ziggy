package com.ziggy.actor
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import com.ziggy.service.{Deliver, DeliveryCommand, FindPartner, OrderConfirmed, PartnerAssigned, PartnerService}

object Delivery {
  def apply(partnerService : PartnerService) : Behavior[DeliveryCommand] = {
    Behaviors.receive {
      (context,message) => {
        message match {
          case Deliver(item,customerRef) => {
          context.log.info(s"Delivering item $item")
            customerRef ! OrderConfirmed(item)
            Behaviors.same
          }
          case FindPartner(orderId) => {
	          partnerService.checkRestaurantAndAssignPartner(orderId)
            Behaviors.same
          }
          case _ => Behaviors.same
        }
      }
    }
  }
}