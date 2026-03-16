package com.ziggy.api

import com.ziggy.database.model.{AddItem, AddItemEvent, AddPartner, AddRestaurant, UpdateItem, UpdateItemEvent, UpdatePartner, UpdateRestaurant, UserType}
import com.ziggy.kafka.{EventMapper, KAFKA_EVENTS, KAFKA_TOPICS}
import com.ziggy.service.DbService
import com.ziggy.utils.security.ZiggySecurity
import io.circe.generic.auto.*
import io.circe.syntax.*
import org.apache.pekko.http.scaladsl.model.StatusCodes
import org.apache.pekko.http.scaladsl.server.Directives.*
import org.apache.pekko.http.scaladsl.server.Route

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class UtilRoutes @Inject()(
	dbService: DbService,
	eventMapper: EventMapper
)(using ec: ExecutionContext)
	extends ZiggySecurity(dbService)(ec)
		with JsonSupport {

	private def publishAccepted(topic: KAFKA_TOPICS, event: KAFKA_EVENTS, payload: String) = {
		onSuccess(eventMapper.sendMessage(topic, event, Some(payload))) { result =>
			complete(StatusCodes.Accepted, result)
		}
	}

	val utilityRoutes: Route = pathPrefix("util") {
		path("add" / "restaurant") {
			post {
				authenticate(Some(UserType.RESTAURANT_ADMIN.toString)) { _ =>
					entity(as[AddRestaurant]) { restaurant =>
						publishAccepted(
							KAFKA_TOPICS.RESTAURANTS,
							KAFKA_EVENTS.ADD_RESTAURANT,
							restaurant.asJson.noSpaces
						)
					}
				}
			}
		} ~
		path("update" / "restaurant") {
			put {
				authenticate(Some(UserType.RESTAURANT_ADMIN.toString)) { _ =>
					entity(as[UpdateRestaurant]) { restaurant =>
						publishAccepted(
							KAFKA_TOPICS.RESTAURANTS,
							KAFKA_EVENTS.UPDATE_RESTAURANT,
							restaurant.asJson.noSpaces
						)
					}
				}
			}
		} ~
		path("add" / "item" / Segment) { restaurantId =>
			post {
				authenticate(Some(UserType.RESTAURANT_ADMIN.toString)) { _ =>
					entity(as[AddItem]) { item =>
						publishAccepted(
							KAFKA_TOPICS.RESTAURANTS,
							KAFKA_EVENTS.ADD_ITEM,
							AddItemEvent(restaurantId, item).asJson.noSpaces
						)
					}
				}
			}
		} ~
		path("update" / "item" / Segment / Segment) { (restaurantId, itemId) =>
			put {
				authenticate(Some(UserType.RESTAURANT_ADMIN.toString)) { _ =>
					entity(as[UpdateItem]) { item =>
						publishAccepted(
							KAFKA_TOPICS.RESTAURANTS,
							KAFKA_EVENTS.UPDATE_ITEM,
							UpdateItemEvent(restaurantId, itemId, item).asJson.noSpaces
						)
					}
				}
			}
		} ~
		path("add" / "partner") {
			post {
				(authenticate(Some(UserType.DELIVERY_PARTNER.toString)) & entity(as[AddPartner])) {
					(user, partner) =>
						val payload = partner.copy(partner = Some(user))
						publishAccepted(
							KAFKA_TOPICS.PARTNER,
							KAFKA_EVENTS.ADD_PARTNER,
							payload.asJson.noSpaces
						)
				}
			}
		} ~
		path("update" / "partner") {
			put {
				(authenticate(Some(UserType.DELIVERY_PARTNER.toString)) & entity(as[UpdatePartner])) {
					(_, partner) =>
						publishAccepted(
							KAFKA_TOPICS.PARTNER,
							KAFKA_EVENTS.UPDATE_PARTNER,
							partner.asJson.noSpaces
						)
				}
			}
		}
	}
}
