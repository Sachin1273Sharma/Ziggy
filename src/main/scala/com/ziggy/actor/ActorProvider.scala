package com.ziggy.actor


import akka.actor.typed.{ActorRef, ActorSystem, Scheduler}
import com.ziggy.service.{CustomerCommand, DeliveryCommand, RestaurantCommand}
import akka.util.Timeout

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration.DurationInt


class ActorProvider(val system : ActorSystem[_])
{
  
  implicit val timeout: Timeout = 3.seconds
  implicit val scheduler: Scheduler = system.scheduler
  implicit val ec: ExecutionContextExecutor = system.executionContext
  
  val deliveryActor : ActorRef[DeliveryCommand] = system.systemActorOf(Delivery(),"delivery")
  val customerActor: ActorRef[CustomerCommand] = system.systemActorOf(Customer(restaurantActor), "customer")
  val restaurantActor : ActorRef[RestaurantCommand] = system.systemActorOf(Restaurant(deliveryActor),"restaurant")
}
