package com.ziggy.api

import com.ziggy.database.model.{AddPartner, AddRestaurant, UpdateRestaurant, UserType}
import com.ziggy.kafka.{EventMapper, KAFKA_EVENTS, KAFKA_TOPICS, KafkaService}
import com.ziggy.service.{DbService, RestaurantService}
import com.ziggy.utils.security.ZiggySecurity
import org.apache.pekko.http.scaladsl.server.Directives.*
import org.apache.pekko.http.scaladsl.server.Route
import io.circe.generic.auto.*
import org.apache.pekko.http.scaladsl.model.StatusCodes

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class UtilRoutes @Inject()(dbService : DbService,
                           eventMapper : EventMapper)(using ec : ExecutionContext)
															extends  ZiggySecurity(dbService)(ec) with JsonSupport {
	val utilityRoutes: Route =
		pathPrefix("util")
		{
			path("add" / "restaurant") {
				authenticate(Some(UserType.RESTAURANT_ADMIN.toString)) {
					user => {
						entity(as[AddRestaurant]) {
							restaurant =>
								onSuccess(eventMapper.sendMessage(KAFKA_TOPICS.RESTAURANTS,
									KAFKA_EVENTS.ADD_RESTAURANT,Some(restaurant.toString))) {
										result => complete(StatusCodes.Accepted,result)
								}
						}
					}
				}
			} ~
				path("update" / "restaurant")  {
						authenticate(Some(UserType.RESTAURANT_ADMIN.toString)) {
							user => {
								entity(as[UpdateRestaurant]) {
									data =>
										onSuccess(
											eventMapper.sendMessage(KAFKA_TOPICS.RESTAURANTS,
												KAFKA_EVENTS.UPDATE_RESTAURANT, Some(data
													.toString))) {
											result =>
												complete(StatusCodes.Accepted, result)
										}
								}
							}
						}
					} ~
			path("add" / "partner"){
				authenticate(Some(UserType.DELIVERY_PARTNER.toString)){
					user => {
						entity(as[AddPartner]) {
							partner => {
								 partner.copy(partner = Some(user))
								eventMapper.sendMessage(KAFKA_TOPICS.PARTNER, KAFKA_EVENTS.ADD_PARTNER,Some(partner
									.toString))
							}
						}
					}
				}
			}
		}
}