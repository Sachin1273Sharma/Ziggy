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

}