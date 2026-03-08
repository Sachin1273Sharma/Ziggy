package com.ziggy.controller

import akka.actor.typed.scaladsl.AskPattern.Askable
import akka.util.Timeout
import com.ziggy.actor.ActorProvider
import com.ziggy.service.{OrderAccepted, OrderConfirmed, OrderDetails, PlaceOrder, PlaceOrderResponse, RestaurantCommand}

import scala.concurrent.Future
import scala.concurrent.duration.DurationInt


class OrderController(actors : ActorProvider)
{
  import actors._

  def placeOrder(order : OrderDetails): Future[RestaurantCommand] = {
    val result: Future[RestaurantCommand] = actors.restaurantActor ? (replyTo => PlaceOrder(order, actors.customerActor, replyTo))
    result
  }
}
