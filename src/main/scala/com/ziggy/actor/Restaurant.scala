package com.ziggy.actor

import org.apache.pekko.actor.typed.{ActorRef, Behavior}
import org.apache.pekko.actor.typed.scaladsl.Behaviors
import com.ziggy.api.JsonSupport
import com.ziggy.service.{DeliveryCommand, FindPartner, OrderAccepted, OrderCreationFailed, OrderService, PartnerAssigned, PlaceOrder, PlaceOrderResponse, RestaurantCommand}
import com.ziggy.utils.Logger
import redis.clients.jedis.Jedis

import scala.concurrent.ExecutionContext

object Restaurant extends JsonSupport with Logger{

  def apply(deliveryRef : ActorRef[DeliveryCommand],
            orderService : OrderService): Behavior[RestaurantCommand] = {
    order(deliveryRef,orderService)
  }

  def order(deliveryRef: ActorRef[DeliveryCommand],orderService: OrderService): Behavior[RestaurantCommand] = {
    Behaviors.receive {
      (context, message) => {
	      given ec: ExecutionContext = context.executionContext
        message match {
          case PlaceOrder(orderDetails, customerRef, replyTo) => {
            val itemSummary = orderDetails.items.mkString(", ")
            log.info(s"We have started preparing items: $itemSummary")
            orderService.createOrder(orderDetails) map{
              case Right(value) => {
	              deliveryRef ! FindPartner(orderId = value,1)
	              log.info(s"OrderId is ${value}")
              }
              case Left(value) =>   replyTo ! OrderCreationFailed
            }
	          Behaviors.same
          }
        }
      }

    }
  }

}
