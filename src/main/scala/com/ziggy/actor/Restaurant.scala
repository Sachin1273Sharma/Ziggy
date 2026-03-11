package com.ziggy.actor

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import com.ziggy.api.JsonSupport
import com.ziggy.service.{DeliveryCommand, DeliveryPartnerAssigned, FindPartner, OrderAccepted, OrderCreationFailed, OrderService, PartnerAssigned, PlaceOrder, PlaceOrderResponse, RestaurantCommand}
import com.ziggy.utils.Logger
import redis.clients.jedis.Jedis

import scala.concurrent.ExecutionContext

object Restaurant extends JsonSupport with Logger{

  def apply(actors : ActorProvider,
            orderService : OrderService): Behavior[RestaurantCommand] = {
    order(actors,orderService)
  }

  def order(actors: ActorProvider,orderService: OrderService): Behavior[RestaurantCommand] = {
    Behaviors.receive {
      (context, message) => {
	      given ec: ExecutionContext = context.executionContext
        message match {
          case PlaceOrder(orderDetails, customerRef, replyTo) => {
            val itemSummary = orderDetails.items.mkString(", ")
            log.info(s"We have started preparing items: $itemSummary")
            orderService.createOrder(orderDetails) map{
              case Right(value) => {
	              actors.deliveryActor ! FindPartner(orderId = value)
	              log.info(s"OrderId is ${value}")
              }
              case Left(value) =>   replyTo ! OrderCreationFailed
            }
	          Behaviors.same
          }
          case PartnerAssigned(orderId,customerRef) => {
            customerRef ! DeliveryPartnerAssigned(order)
            Behaviors.same
          }
          case OrderCreationFailed => {

          }
        }
      }

    }
  }

}
