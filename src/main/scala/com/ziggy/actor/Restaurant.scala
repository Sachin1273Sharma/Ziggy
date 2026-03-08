package com.ziggy.actor

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import com.ziggy.api.JsonSupport
import com.ziggy.service.{DeliveryCommand, DeliveryPartnerAssigned, FindPartner, OrderAccepted, PartnerAssigned, PlaceOrder, PlaceOrderResponse, RestaurantCommand}
import redis.clients.jedis.Jedis

object Restaurant extends JsonSupport {

  private val jedis = new Jedis("localhost", 6379)

  def apply(deliveryRef: ActorRef[DeliveryCommand]): Behavior[RestaurantCommand] = {
    order(deliveryRef, 0)
  }

  def order(deliveryRef: ActorRef[DeliveryCommand], totalOrders: Int): Behavior[RestaurantCommand] = {
    Behaviors.receive {
      (context, message) => {
        message match {
          case PlaceOrder(orderDetails, customerRef, replyTo) => {
            val itemSummary = orderDetails.item.mkString(", ")
            context.log.info(s"We have started preparing items: $itemSummary")
            val orderId = jedis.incr("customer:order:id").toString
            val id = s"ORD-$orderId"
            customerRef ! OrderAccepted(itemSummary, id)
            context.log.info(s"OrderId is $orderId")
            deliveryRef ! FindPartner(itemSummary, id, context.self)
            replyTo ! PlaceOrderResponse(s"Order placed successfully with orderId $orderId . Thank you ${orderDetails.name}" )
            order(deliveryRef, totalOrders + 1)
          }
          case PartnerAssigned(orderId,customerRef) => {
            context.log.info(s"Partner assigned for order $orderId. Total orders: $totalOrders")
            customerRef ! DeliveryPartnerAssigned(order)
            Behaviors.same
          }
        }
      }

    }
  }

}
