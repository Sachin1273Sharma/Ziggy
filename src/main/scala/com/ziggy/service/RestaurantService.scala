package com.ziggy.service

import com.ziggy.database.model.AddRestaurant
import com.ziggy.utils.Logger

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class RestaurantService @Inject()
															  ()(using ec: ExecutionContext) extends Logger {



	def addRestaurant(data : AddRestaurant):   = {

	}
}