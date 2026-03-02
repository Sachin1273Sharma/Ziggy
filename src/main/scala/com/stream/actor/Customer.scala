package com.stream.actor

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import com.stream.service.{CustomerCommand, OrderAccepted, PlaceOrder, RestaurantCommand, Order}


object Customer {

  def apply(ref : ActorRef[RestaurantCommand]) : Behavior[CustomerCommand | OrderAccepted] = {

    Behaviors.receive{
      (context,message) => {
      message match {
        case Order(item) => {
            ref ! PlaceOrder(item,context.self)
            Behaviors.same
        }
        case OrderAccepted(item) => {
          context.log.info(s"Order accepted for $item")
          Behaviors.same
        }
        case DeliveryPartnerAssigned(order : OrderDetails)   => {
          
        }
        case _ => {
          Behaviors.same
        }
      }
    }}
  }

}