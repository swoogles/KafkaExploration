package com.billding.kafka

import java.util.Collections
import org.apache.kafka.clients.consumer.KafkaConsumer

class SimpleConsumer(
  input: String
) {
  val kafkaConfig: KafkaConfig = new KafkaConfig()
  val consumer: KafkaConsumer[String, String] = new KafkaConsumer[String, String](kafkaConfig.props)

  consumer.subscribe(Collections.singletonList(input))

  def poll(timeout: Long) =
    consumer.poll(timeout)

}

