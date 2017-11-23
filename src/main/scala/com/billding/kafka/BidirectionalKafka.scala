package com.billding.kafka

import play.api.libs.json.JsValue

class BidirectionalKafka(
  input: String,
  output: String
) {
  val simpleConsumer = new SimpleConsumer(input)

  def poll(timeout: Long) =
    simpleConsumer.poll(timeout)

  val simpleProducer = new SimpleProducer(output)

  def send(key: String , value: JsValue ) =
    simpleProducer.send(key, value)
}
