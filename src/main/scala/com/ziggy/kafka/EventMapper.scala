package com.ziggy.kafka

import com.ziggy.kafka.KAFKA_EVENTS.ADD_RESTAURANT

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class EventMapper @Inject()(kafkaService : KafkaService) {

	def sendMessage(topic : KAFKA_TOPICS, event: KAFKA_EVENTS, data: Option[String]): Future[String] = {
		event match {
			case ADD_RESTAURANT => kafkaService.publishEvent(topic.toString,event.toString,data)
		}
		Future.successful("PUBLISHED")
}

	def suscribeMessage(topic: KAFKA_TOPICS, event: KAFKA_EVENTS,
	                    data: KAFKA_DATA): Future[String] = {
		topic match {
			case KAFKA_TOPICS.RESTAURANTS => processRestrauntMessages(event, data)

			case KAFKA_TOPICS.PARTNER => processPartnerMessages(event,data)
			case KAFKA_TOPICS.CUSTOMER => {}
		}
	}

	def processRestrauntMessages(event : KAFKA_EVENTS,data : KAFKA_DATA) = {
		 event match {
			 case KAFKA_EVENTS.ADD_RESTAURANT => kafkaService.addRestaurant(data)
			 case KAFKA_EVENTS.UPDATE_RESTAURANT => {}
		 }
	}
	def processPartnerMessages(event : KAFKA_EVENTS,data : KAFKA_DATA): Future[String] = {
		Future.successful("")
	}
	def processCustomerMessages(event : KAFKA_EVENTS,data : KAFKA_DATA) = {
		Future.successful("")
	}

}