package com.ziggy.kafka

import org.apache.kafka.clients.consumer.Consumer
import org.apache.pekko.kafka.{ConsumerSettings, Subscriptions}
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.pekko.actor.typed.ActorSystem
import org.apache.pekko.kafka.scaladsl.Consumer
import org.apache.pekko.stream.scaladsl.Sink

import javax.inject.{Inject, Singleton}

@Singleton
class KafkaConsumer @Inject()
()(using system: ActorSystem[_]) {

	private val consumerSettings: ConsumerSettings[String, String] = ConsumerSettings(system,
		new StringDeserializer,
		new StringDeserializer)
		.withBootstrapServers("localhost:9092")
		.withGroupId("ziggy-restaurant-group") // Unique group name
		.withProperty(org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

	val consumer: Consumer[String, String] = consumerSettings.createKafkaConsumer()

	def receive()
}