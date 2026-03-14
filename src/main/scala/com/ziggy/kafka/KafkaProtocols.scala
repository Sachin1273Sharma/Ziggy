package com.ziggy.kafka

enum KAFKA_TOPICS(val value : String) :
	case RESTAURANTS extends KAFKA_TOPICS("restaurant")
	case CUSTOMER extends KAFKA_TOPICS("customer")
	case PARTNER extends KAFKA_TOPICS("partner")

enum KAFKA_EVENTS(val value : String) :
	case ADD_RESTAURANT extends KAFKA_EVENTS("add_restaurant")
	case UPDATE_RESTAURANT extends KAFKA_EVENTS("update_restaurant")

enum KAFKA_DATA(val value : String):
	case NOTHING extends KAFKA_DATA("nothing")
