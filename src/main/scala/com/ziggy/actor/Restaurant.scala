package com.ziggy.actor

import com.ziggy.actor.Restaurant.log
import org.apache.pekko.actor.typed.{ActorRef, Behavior}
import org.apache.pekko.actor.typed.scaladsl.Behaviors
import com.ziggy.api.JsonSupport
import com.ziggy.service.{DeliveryCommand, FindPartner, OrderAccepted, OrderCreationFailed, OrderService, PartnerAssigned, PlaceOrder, PlaceOrderResponse, RestaurantCommand}
import com.ziggy.utils.Logger
import redis.clients.jedis.Jedis

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton class Restaurant @Inject()(orderService : OrderService,
                                      deliveryRef : ActorRef[DeliveryCommand])
                                     (using ec: ExecutionContext) extends
                                                                  JsonSupport
                                                                  with Logger {
def behavior() : Behavior[RestaurantCommand]  = Behaviors.receive {
	(context,message) => {
		message match {
			case PlaceOrder(orderDetails, customerRef, replyTo) => {
				val itemSummary = orderDetails.items.mkString(", ")
				log.info(s"We have started preparing items: $itemSummary")
				orderService.createOrder(orderDetails) map {
					case Right(value) => {
						deliveryRef ! FindPartner(orderId = value, 1)
						log.info(s"OrderId is ${value}")
					}
					case Left(value) => replyTo ! OrderCreationFailed
				}
				Behaviors.same
			}
		}
	}
}

}

