package com.ziggy.api


import org.apache.pekko.actor.typed.Scheduler
import org.apache.pekko.actor.typed.scaladsl.AskPattern.{Askable, schedulerFromActorSystem}
import org.apache.pekko.http.scaladsl.model.StatusCodes
import org.apache.pekko.http.scaladsl.server.Directives.*
import org.apache.pekko.http.scaladsl.server.*
import org.apache.pekko.util.Timeout
import com.ziggy.actor.ActorProvider
import com.ziggy.controller.OrderController
import com.ziggy.database.model.{OrderRequest, User, UserType}
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
			authenticate(Some(UserType.CUSTOMER.toString)) { user => {
				post {
					entity(as[OrderRequest]) { order => {
						log.info(s"Customer ${user.name.getOrElse("")} \n order : ${order}")

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
					authenticate(Some(UserType.DELIVERY_PARTNER.toString)) { user =>
						actors.deliveryActor ! Delivered(orderId)
						complete(StatusCodes.NoContent)
					}
				}
			})
	}
}