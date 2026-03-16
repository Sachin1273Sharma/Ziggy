package com.ziggy.actor


import com.ziggy.service.{DeliveryCommand, RestaurantCommand}
import org.apache.pekko.actor.typed.ActorRef

import javax.inject.{Inject, Singleton}

@Singleton
class ActorProvider @Inject(
  val restaurantActor: ActorRef[RestaurantCommand],
  val deliveryActor: ActorRef[DeliveryCommand]
)

