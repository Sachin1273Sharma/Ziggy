package com.ziggy.actor


import org.apache.pekko.actor.typed.{ActorRef, ActorSystem, Scheduler}
import com.ziggy.service.{CustomerCommand, DeliveryCommand, OrderService, PartnerService, RestaurantCommand}
import org.apache.pekko.util.Timeout

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration.DurationInt

@Singleton
class ActorProvider@Inject(partnerService : PartnerService ,
                           orderService : OrderService)
                          (val system : ActorSystem[_])
{
  
  implicit val timeout: Timeout = 3.seconds
  implicit val scheduler: Scheduler = system.scheduler
  implicit val ec: ExecutionContextExecutor = system.executionContext
  
  val deliveryActor : ActorRef[DeliveryCommand] = system.systemActorOf(Delivery(partnerService,orderService),
	  "delivery")
  val customerActor: ActorRef[CustomerCommand] = system.systemActorOf(Customer(), "customer")
  val restaurantActor : ActorRef[RestaurantCommand] = system.systemActorOf(Restaurant(deliveryActor,orderService),
	  "restaurant")
}
