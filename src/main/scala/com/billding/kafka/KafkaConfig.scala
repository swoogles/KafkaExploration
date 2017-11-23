package com.billding.kafka

import java.time.Clock
import java.util.Properties

/*
  There should be some capability here for making sure these
  topics/servers are actually available.
 */
class KafkaConfig {
  val  props = new Properties()
  props.put("bootstrap.servers", "localhost:9092")

  props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
  props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
  props.put("group.id", "something")

  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

  val RAW_WEATHER = "raw_weather"
  val BUSINESS_TOPIC = "business_topic"
  val PLEASURE_TOPIC = "pleasure_topic"

  val clock = Clock.systemUTC()
}

object KafkaConfigPermanent {
  val  props = new Properties()
  props.put("bootstrap.servers", "localhost:9092")

  props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
  props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
  props.put("group.id", "something")

  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

  val RAW_WEATHER = "raw_weather"
  val BUSINESS_TOPIC = "business_topic"
  val PLEASURE_TOPIC = "pleasure_topic"
}
