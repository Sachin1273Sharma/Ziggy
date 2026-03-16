package com.ziggy.kafka

import com.ziggy.api.JsonSupport
import com.ziggy.database.model.{AddItemEvent, AddPartner, AddRestaurant, UpdateItemEvent, UpdatePartner, UpdateRestaurant}
import com.ziggy.service.{PartnerService, RestaurantService}
import io.circe.generic.auto.*
import io.circe.parser.decode

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class KafkaService @Inject()(
  restaurantService: RestaurantService,
  kafkaProducer: KafkaProducer,
  partnerService: PartnerService
)(using ec: ExecutionContext)
    extends JsonSupport {

  def publishEvent(topic: String, event: String, data: Option[String]): Unit = {
    data match {
      case Some(value) => kafkaProducer.send(topic, event, value)
      case None => kafkaProducer.send(topic, event, KAFKA_DATA.NOTHING.value)
    }
  }

  def addRestaurant(data: KAFKA_DATA): Future[Boolean] = {
    decode[AddRestaurant](data.value) match {
      case Right(value) => restaurantService.addRestaurant(value).map(_ => true)
      case Left(exception) => Future.failed(exception)
    }
  }

  def updateRestaurant(data: KAFKA_DATA): Future[Boolean] = {
    decode[UpdateRestaurant](data.value) match {
      case Right(value) => restaurantService.updateRestaurant(value)
      case Left(exception) => Future.failed(exception)
    }
  }

  def addPartner(data: KAFKA_DATA): Future[Boolean] = {
    decode[AddPartner](data.value) match {
      case Right(value) => partnerService.addPartner(value).map(_ => true)
      case Left(exception) => Future.failed(exception)
    }
  }

  def updatePartner(data: KAFKA_DATA): Future[Boolean] = {
    decode[UpdatePartner](data.value) match {
      case Right(value) => partnerService.updatePartner(value)
      case Left(exception) => Future.failed(exception)
    }
  }

  def addItem(data: KAFKA_DATA): Future[Boolean] = {
    decode[AddItemEvent](data.value) match {
      case Right(value) => restaurantService.addItem(value).map(_ => true)
      case Left(exception) => Future.failed(exception)
    }
  }

  def updateItem(data: KAFKA_DATA): Future[Boolean] = {
    decode[UpdateItemEvent](data.value) match {
      case Right(value) => restaurantService.updateItem(value)
      case Left(exception) => Future.failed(exception)
    }
  }
}
