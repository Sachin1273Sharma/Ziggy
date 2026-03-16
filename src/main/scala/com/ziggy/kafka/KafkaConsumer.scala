package com.ziggy.kafka

import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.pekko.actor.typed.ActorSystem
import org.apache.pekko.kafka.scaladsl.{Committer, Consumer}
import org.apache.pekko.kafka.{CommitterSettings, ConsumerSettings, Subscriptions}
import org.apache.pekko.stream.{ActorAttributes, Supervision}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class KafkaConsumer @Inject()(eventMapper: EventMapper)(
  using system: ActorSystem[_]
)(using ec: ExecutionContext) {

  private val committerSettings = CommitterSettings(system)

  private val supervise: Supervision.Decider = {
    case _: Exception => Supervision.restart
    case _ => Supervision.stop
  }

  private val consumerSettings: ConsumerSettings[String, String] =
    ConsumerSettings(system, new StringDeserializer, new StringDeserializer)
      .withBootstrapServers("localhost:9092")
      .withGroupId("ziggy-restaurant-group")
      .withProperty(
        org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
        "earliest"
      )

  Consumer
    .committableSource(
      consumerSettings,
      Subscriptions.topics(
        KAFKA_TOPICS.RESTAURANTS.value,
        KAFKA_TOPICS.PARTNER.value,
        KAFKA_TOPICS.CUSTOMER.value
      )
    )
    .mapAsync(5) { msg =>
      val maybeTopic = KAFKA_TOPICS.fromValue(msg.record.topic())
      val maybeEvent = KAFKA_EVENTS.fromValue(msg.record.key())

      (maybeTopic, maybeEvent) match {
        case (Some(topic), Some(event)) =>
          eventMapper
            .subscribeMessage(topic, event, KAFKA_DATA(msg.record.value()))
            .map(_ => msg.committableOffset)
        case _ =>
          Future.failed(
            new IllegalArgumentException(
              s"Unsupported kafka message topic=${msg.record.topic()} event=${msg.record.key()}"
            )
          )
      }
    }
    .withAttributes(ActorAttributes.supervisionStrategy(supervise))
    .runWith(Committer.sink(committerSettings))
}
