package com.billding.kafka

import java.util

import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

class BidirectionalKafka(
  input: String,
  output: String
) {
  val kafkaConfig: KafkaConfig = new KafkaConfig()
  val consumer: KafkaConsumer[String, String] = new KafkaConsumer[String, String](kafkaConfig.props)

  consumer.subscribe(util.Collections.singletonList(input))

  val producer: KafkaProducer[String, String] = new KafkaProducer[String, String](kafkaConfig.props)

  def send(key: String , value: String ) = {
    producer.send(
      new ProducerRecord(
        output, key, value)
    )
  }
}
