package com.billding.kafka

import com.billding.serialization.JsonSerializer
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.common.serialization.StringSerializer
import play.api.libs.json.JsValue

class SimpleProducer (
  output: String
)
{
  val kafkaConfig: KafkaConfig = new KafkaConfig()

  val producer: KafkaProducer[String, JsValue] =
    new KafkaProducer(kafkaConfig.props, new StringSerializer, new JsonSerializer[JsValue])

  def send(key: String , value: JsValue ) = {
    producer.send(
      new ProducerRecord(
        output, key, value)
    )
  }
}
