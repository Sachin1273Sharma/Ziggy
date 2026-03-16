package com.ziggy

import org.apache.pekko.actor.typed.{ActorRef, ActorSystem, Behavior}
import org.apache.pekko.actor.typed.scaladsl.Behaviors
import org.apache.pekko.http.scaladsl.Http
import com.google.inject.Guice
import com.ziggy.actor.{ActorProvider, Delivery, Restaurant}
import com.ziggy.api.routes
import com.ziggy.service.{DeliveryCommand, RestaurantCommand}

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}
import scala.io.StdIn
import scala.util.{Failure, Success}

def main(args: Array[String]): Unit = {

  val rootBehavior: Behavior[Nothing] = Behaviors.empty

  implicit val system: ActorSystem[Nothing] =
    ActorSystem(
      rootBehavior,
      "Ziggy"
      )

  implicit val ec: ExecutionContextExecutor =
    system.executionContext

  val injector = Guice.createInjector(new AppModule(system))
   val deliveryInjector: Delivery = injector.getInstance(classOf[Delivery])
  val deliveryActor: ActorRef[DeliveryCommand] =
    system.systemActorOf(deliveryInjector.behavior, "delivery")
   val restaurantInjector = injector.getInstance(classOf[Restaurant])
  val restaurantActor: ActorRef[RestaurantCommand] = system.systemActorOf(
    restaurantInjector.behavior(),
    "restaurant")

  val appRoutes = injector.getInstance(classOf[routes])

  val bindingFuture =
    Http()
      .newServerAt(
        "localhost",
        8080
        )
      .bind(appRoutes.routes)

  println("----------------------------------------------")
  println("🚀 ZIGGY BACKEND IS ONLINE!")
  println("🔗 URL: http://localhost:8080/api/health")

  StdIn.readLine()

  bindingFuture
    .flatMap(_.unbind())
    .onComplete {
      case Success(_) =>
        println("Stopping ActorSystem...")
        system.terminate()

      case Failure(ex) =>
        println(s"Error while stopping: ${ex.getMessage}")
        system.terminate()
    }
}