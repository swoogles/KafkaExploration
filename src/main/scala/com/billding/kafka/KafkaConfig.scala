package com.billding.kafka

import java.util.Properties

class KafkaConfig {
  val  props = new Properties()
  props.put("bootstrap.servers", "localhost:9092")

  props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
  props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
  props.put("group.id", "something")

  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

  val TOPIC_1 = "test2"
  val TOPIC_2 = "filtered_topic"
}
