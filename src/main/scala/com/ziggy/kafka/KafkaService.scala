package com.ziggy.kafka

import javax.inject.{Inject, Singleton}

@Singleton
class KafkaService @Inject()(kafkaProducer : KafkaProducer) {

	def publishEvent(topic : String , event : String ,data : Option[String]): Unit = {
		data match {
			case Some(value) => kafkaProducer.send(topic ,event,value)
			case _ => kafkaProducer.send(topic,event,KAFKA_DATA.NOTHING.value)
		}

	}
}