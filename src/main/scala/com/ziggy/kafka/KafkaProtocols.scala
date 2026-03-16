package com.ziggy.kafka

enum KAFKA_TOPICS(val value : String) :
	case RESTAURANTS extends KAFKA_TOPICS("restaurant")
	case CUSTOMER extends KAFKA_TOPICS("customer")
	case PARTNER extends KAFKA_TOPICS("partner")

object KAFKA_TOPICS {
	def fromValue(value: String): Option[KAFKA_TOPICS] =
		KAFKA_TOPICS.values.find(_.value == value)
}

enum KAFKA_EVENTS(val value : String) :
	case ADD_RESTAURANT extends KAFKA_EVENTS("add_restaurant")
	case UPDATE_RESTAURANT extends KAFKA_EVENTS("update_restaurant")
	case ADD_PARTNER extends KAFKA_EVENTS("add_partner")
	case UPDATE_PARTNER extends KAFKA_EVENTS("update_partner")
	case ADD_ITEM extends KAFKA_EVENTS("add_item")
	case UPDATE_ITEM extends KAFKA_EVENTS("update_item")

object KAFKA_EVENTS {
	def fromValue(value: String): Option[KAFKA_EVENTS] =
		KAFKA_EVENTS.values.find(_.value == value)
}

final case class KAFKA_DATA(value : String)

object KAFKA_DATA {
	val NOTHING: KAFKA_DATA = KAFKA_DATA("nothing")
}
