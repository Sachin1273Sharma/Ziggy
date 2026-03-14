package com.ziggy.kafka

import org.apache.kafka.clients.producer.Producer
import org.apache.pekko.kafka.ProducerSettings
import org.apache.kafka.common.serialization.StringSerializer
import org.apache.pekko.actor.typed.ActorSystem
import org.apache.kafka.clients.producer.ProducerRecord

import javax.inject.{Inject, Singleton}

@Singleton
class KafkaProducer @Inject()()(using system : ActorSystem[_]) {
	val producerSettings: ProducerSettings[String, String] = ProducerSettings(system, new StringSerializer, new StringSerializer)
		.withBootstrapServers("localhost:9092")
	val producer: Producer[String, String] = producerSettings.createKafkaProducer()

	def send(topic : String,key : String,value : String) = {
		val record = new ProducerRecord[String, String](topic, key, value)
		producer.send(record)
	}
}