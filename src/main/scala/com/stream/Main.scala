package com.stream

import akka.actor.typed.{ActorSystem, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import com.stream.actor.{Customer, Restaurant}
import com.stream.service.{PlaceOrder, Order}

object Main {
    def main(args : Array[String]) : Unit = {
      val rootBehavior : Behavior[Nothing] = Behaviors.setup[Nothing]  {
        context => {
          val restaurant = context.spawn(Restaurant(),"restaurant")
          val customer = context.spawn(Customer(restaurant),"customer")
          val order = List("Burger","Pizza","Dosa")
          order.foreach(x => {
            customer ! Order(x)
          })
     
          Behaviors.empty
        }
      }
      val system = ActorSystem[Nothing](rootBehavior, "FoodSystem")
      Thread.sleep(2000)
      system.terminate()
    }
}