package com.ziggy.api


import akka.actor.typed.Scheduler
import akka.actor.typed.scaladsl.AskPattern.{Askable, schedulerFromActorSystem}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.server.*
import akka.util.Timeout
import com.ziggy.actor.ActorProvider
import com.ziggy.controller.OrderController
import com.ziggy.database.model.{Customer, OrderRequest}
import com.ziggy.service.{DbService, Delivered, OrderCreationFailed, PlaceOrder}
import com.ziggy.utils.Logger
import com.ziggy.utils.security.ZiggySecurity
import io.circe.generic.auto.*

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext
import scala.util.Success

@Singleton
class OrderRoutes @Inject()(
	                           actors: ActorProvider,
	                           dbService: DbService,
	                           orderController: OrderController
                           )(
	                                      using ec: ExecutionContext
                                      )(using timeout: Timeout)(using scheduler: Scheduler) extends ZiggySecurity(
	dbService)(ec) with JsonSupport with Logger {
	val routes: Route = pathPrefix("order") {
		Directives.concat(path("place") {
			authenticateOAuth2Async[Customer]("Unauthorized", validateLoginCredentials) { customer => {
				post {
					entity(as[OrderRequest]) { order => {
						log.info(s"Customer ${customer.name.getOrElse("")} \n order : ${order}")

						onComplete(orderController.placeOrder(order)) { case Success(OrderCreationFailed) => complete(
							StatusCodes.InternalServerError,
							"Something went wrong. Please try again later")
						case _ => complete(StatusCodes.OK, "Order Created")
						}
					}
					}
				}
			}
			}
		},
			path("delivered" / Segment){
				orderId => {
					actors.deliveryActor ! Delivered(orderId)
					complete(StatusCodes.NoContent)
				}
			})
	}
}