package com.ziggy.kafka

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class EventMapper @Inject()(kafkaService: KafkaService) {

  def sendMessage(topic: KAFKA_TOPICS, event: KAFKA_EVENTS, data: Option[String]): Future[String] = {
    kafkaService.publishEvent(topic.value, event.value, data)
    Future.successful("PUBLISHED")
  }

  def subscribeMessage(topic: KAFKA_TOPICS, event: KAFKA_EVENTS, data: KAFKA_DATA): Future[Boolean] = {
    topic match {
      case KAFKA_TOPICS.RESTAURANTS => processRestaurantMessages(event, data)
      case KAFKA_TOPICS.PARTNER => processPartnerMessages(event, data)
      case KAFKA_TOPICS.CUSTOMER => Future.successful(false)
    }
  }

  private def processRestaurantMessages(event: KAFKA_EVENTS, data: KAFKA_DATA): Future[Boolean] = {
    event match {
      case KAFKA_EVENTS.ADD_RESTAURANT => kafkaService.addRestaurant(data)
      case KAFKA_EVENTS.UPDATE_RESTAURANT => kafkaService.updateRestaurant(data)
      case KAFKA_EVENTS.ADD_ITEM => kafkaService.addItem(data)
      case KAFKA_EVENTS.UPDATE_ITEM => kafkaService.updateItem(data)
      case _ => Future.successful(false)
    }
  }

  private def processPartnerMessages(event: KAFKA_EVENTS, data: KAFKA_DATA): Future[Boolean] = {
    event match {
      case KAFKA_EVENTS.ADD_PARTNER => kafkaService.addPartner(data)
      case KAFKA_EVENTS.UPDATE_PARTNER => kafkaService.updatePartner(data)
      case _ => Future.successful(false)
    }
  }

  def processCustomerMessages(event: KAFKA_EVENTS, data: KAFKA_DATA): Future[Boolean] = {
    Future.successful(false)
  }
}
