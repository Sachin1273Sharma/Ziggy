package com.ziggy.kafka

import com.ziggy.api.JsonSupport
import com.ziggy.database.model.{AddPartner, AddRestaurant, UpdatePartner, UpdateRestaurant}
import com.ziggy.service.{PartnerService, RestaurantService}
import io.circe.Json

import javax.inject.{Inject, Singleton}
import io.circe.*
import io.circe.parser.*
import io.circe.generic.auto.*

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Success

@Singleton
class KafkaService @Inject()(restaurantService: RestaurantService, kafkaProducer: KafkaProducer,
                             partnerService : PartnerService)
                            (using ec : ExecutionContext)
	extends
	JsonSupport {

	def publishEvent(topic: String, event: String, data: Option[String]): Unit = {
		data match {
			case Some(value) => kafkaProducer.send(topic, event, value)
			case _ => kafkaProducer.send(topic, event, KAFKA_DATA.NOTHING.value)
		}
	}

	def addRestaurant(data: KAFKA_DATA):Future[Boolean] = {
		val result = decode[AddRestaurant](data.value) map {
			value => restaurantService.addRestaurant(value)
		}
		result match {
			case Right(value) => value map {
				x => true
			}
			case _ => Future.failed(new Exception("Failed to update"))
		}
	}

	def updateRestaurant(data : KAFKA_DATA) : Future[Boolean] = {
		val result = decode[UpdateRestaurant](data.value) map {
			value => restaurantService.updateRestaurant(value)
		}
		result match {
			case Right(value) => value
			case _ => Future.failed(new Exception("Failed to update"))
		}
	}

	def addPartner(data : KAFKA_DATA): Future[Boolean] = {
		val result = decode[AddPartner](data.value) map {
			value => partnerService.addPartner(value)
		}
		result match {
			case Right(value) => Future.successful(true)
			case _ => Future.failed(new Exception("Failed to Add"))
		}
	}

	def updatePartner(data: KAFKA_DATA): Future[Boolean] = {
		val result = decode[UpdatePartner](data.value) map
		             {
			             value => partnerService.updatePartner(value)
		             }
		result match {
			case Left(exception: Exception) => Future.failed(exception)
			case Right(value) => value
		}
	}

}