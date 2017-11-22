package com.billding.kafka

import java.util
import java.util.Properties

import com.billding.serialization.JsonSerializer
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
import org.apache.kafka.common.serialization.StringSerializer
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

//    new KafkaProducer[String, JsValue](kafkaConfig.props)

  new KafkaProducer(kafkaConfig.props, new StringSerializer, new JsonSerializer[JsValue])

  def send(key: String , value: JsValue ) = {
    producer.send(
      new ProducerRecord(
        output, key, value)
    )
  }
}
