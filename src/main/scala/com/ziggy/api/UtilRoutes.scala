package com.ziggy.api

import com.ziggy.database.model.{AddRestaurant, UserType}
import com.ziggy.service.DbService
import com.ziggy.utils.security.ZiggySecurity
import org.apache.pekko.http.scaladsl.server.Directives.*
import org.apache.pekko.http.scaladsl.server.Route
import io.circe.generic.auto.*

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class UtilRoutes @Inject()(dbService : DbService,
                           restaurantService : RestaurantService)(using ec : ExecutionContext)
															extends  ZiggySecurity(dbService)(ec) with JsonSupport {
	val utilityRoutes: Route =
		pathPrefix("util")
		{
			path("add" / "restaurant" / Segment) {
				authenticate(Some(UserType.RESTAURANT_ADMIN.toString)) {
					user => {
						entity(as[AddRestaurant]) {
							restaurant =>
						}
					}
				}
			}
		}
}