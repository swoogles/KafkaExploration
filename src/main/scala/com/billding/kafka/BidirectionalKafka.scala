package com.billding.kafka

import java.util

import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import play.api.libs.json.JsValue

class BidirectionalKafka(
  input: String,
  output: String
) {
  val kafkaConfig: KafkaConfig = new KafkaConfig()
  val consumer: KafkaConsumer[String, String] = new KafkaConsumer[String, String](kafkaConfig.props)

  consumer.subscribe(util.Collections.singletonList(input))

  def poll(timeout: Long) = {
    println("polling on: " + input)
    consumer.poll(timeout)
  }

  val producer: KafkaProducer[String, JsValue] =
    new KafkaProducer[String, JsValue](kafkaConfig.props)

  def send(key: String , value: JsValue ) = {
    producer.send(
      new ProducerRecord(
        output, key, value)
    )
  }
}
