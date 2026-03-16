package com.ziggy

import org.apache.pekko.actor.typed.{ActorSystem, Behavior}
import org.apache.pekko.actor.typed.scaladsl.Behaviors
import org.apache.pekko.http.scaladsl.Http
import com.google.inject.Guice
import com.ziggy.actor.{Delivery, Restaurant}
import com.ziggy.api.routes


import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn
import scala.util.{Failure, Success}

def main(args: Array[String]): Unit = {

	val rootBehavior: Behavior[Nothing] = Behaviors.empty

	implicit val system: ActorSystem[Nothing] =
		ActorSystem(rootBehavior, "Ziggy")

	implicit val ec: ExecutionContextExecutor =
		system.executionContext

	val injector = Guice.createInjector(new AppModule(system))

	val appRoutes = injector.getInstance(classOf[routes])

	// actors from guice
	val restaurantActor = injector.getInstance(classOf[Restaurant])
	val deliveryActor = injector.getInstance(classOf[Delivery])

	// spawn actors
	val restaurant =
		system.systemActorOf(
			restaurantActor.behavior(),
			"restaurant"
			)

	val delivery =
		system.systemActorOf(
			deliveryActor.behavior,
			"delivery"
			)

	val bindingFuture =
		Http().newServerAt("localhost", 8080).bind(appRoutes.routes)

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