package com.ziggy.controller

import org.apache.pekko.actor.typed.scaladsl.AskPattern.Askable
import org.apache.pekko.util.Timeout
import com.ziggy.actor.ActorProvider
import com.ziggy.database.model.OrderRequest
import com.ziggy.service.{OrderAccepted, OrderConfirmed, PlaceOrder, PlaceOrderResponse,
  RestaurantCommand}

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

@Singleton
class OrderController @Inject(actors: ActorProvider) {

  import actors._

  def placeOrder(order: OrderRequest): Future[RestaurantCommand] = {
    actors.restaurantActor ?
    (replyTo => PlaceOrder(
      order,
      replyTo
      ))
  }
}
