package com.ziggy.kafka


import org.apache.pekko.kafka.{CommitterSettings, ConsumerSettings, Subscriptions}
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.pekko.actor.typed.ActorSystem
import org.apache.pekko.kafka.scaladsl.{Committer, Consumer}
import org.apache.pekko.stream.{ActorAttributes, Supervision}

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt

@Singleton class KafkaConsumer @Inject()(eventMapper: EventMapper)
                                        (using system: ActorSystem[_])
                                        (using ec: ExecutionContext) {

	val committerSettings = CommitterSettings(system)
	private val supervise: Supervision.Decider = {
		case ex: Exception => Supervision.restart
		case _ => Supervision.stop
	}

	private val consumerSettings: ConsumerSettings[String, String] = ConsumerSettings(system,
	                                                                                  new
			                                                                                  StringDeserializer,
	                                                                                  new
			                                                                                  StringDeserializer)
		.withBootstrapServers(
			"localhost:9092")
		.withGroupId(
			"ziggy-restaurant-group") // Unique group name
		.withProperty(
			org
				.apache
				.kafka
				.clients
				.consumer
				.ConsumerConfig
				.AUTO_OFFSET_RESET_CONFIG,
			"earliest")
	Consumer.committableSource(consumerSettings, Subscriptions.topics(KAFKA_TOPICS
		                                                                  .RESTAURANTS
		                                                                  .value, KAFKA_TOPICS
		                                                                  .PARTNER
		                                                                  .value, KAFKA_TOPICS
		                                                                  .CUSTOMER
		                                                                  .value))
	        .mapAsync(5) { msg =>
		        val result = eventMapper.suscribeMessage(KAFKA_TOPICS.valueOf(msg.record
		                                                                         .topic()),
		                                                 KAFKA_EVENTS.valueOf(msg.record
		                                                                         .key()),
		                                                 KAFKA_DATA.valueOf(msg.record
		                                                                       .value()))
		        result.map(_ => msg._2)
	        }.withAttributes(ActorAttributes.supervisionStrategy(supervise)).runWith(Committer.sink
	                                                                                          (committerSettings))

}