package com.ziggy.actor

import org.apache.pekko.actor.typed.{ActorRef, Behavior}
import org.apache.pekko.actor.typed.scaladsl.Behaviors
import com.ziggy.service.{CustomerCommand, OrderAccepted, PlaceOrder, RestaurantCommand, Order}


object Customer {

  def apply() : Behavior[CustomerCommand | OrderAccepted] = {

    Behaviors.receive{
      (context,message) => {
      message match {
        case _ => {
          Behaviors.same
        }
      }
    }}
  }

}