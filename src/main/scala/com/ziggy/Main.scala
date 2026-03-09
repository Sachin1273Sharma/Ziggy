package com.ziggy

import akka.actor.typed.{ActorSystem, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import com.google.inject.Guice
import com.ziggy.actor.{Customer, Delivery, Restaurant}
import com.ziggy.api.routes

import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn
import scala.util.{Failure, Success}

object Main {
    def main(args : Array[String]) : Unit = {
      val rootBehavior : Behavior[Nothing] = Behaviors.setup[Nothing]  {
        context => {
          val delivery = context.spawn(Delivery(),"delivery")
          val restaurant = context.spawn(Restaurant(delivery),"restaurant")
          val customer = context.spawn(Customer(restaurant),"customer")
          Behaviors.empty
        }
      }

      implicit val system : ActorSystem[Nothing] = ActorSystem[Nothing](rootBehavior, "Ziggy")
      implicit val ec: ExecutionContextExecutor = system.executionContext
      val injector = Guice.createInjector(new AppModule(system))
      val appRoutes = injector.getInstance(classOf[routes])
      val bindingFuture = Http().newServerAt("localhost", 8080).bind(appRoutes.routes)
      println("----------------------------------------------")
      println("🚀 ZIGGY BACKEND IS ONLINE!")
      println("🔗 URL: http://localhost:8080/api/health")
      StdIn.readLine()
      bindingFuture
        .flatMap(_.unbind()) // Port free karo
        .onComplete {
          case Success(_) =>
            println("Stopping ActorSystem...")
            system.terminate()
          case Failure(ex) =>
            println(s"Error while stopping: ${ex.getMessage}")
            system.terminate()
        }
    }
}