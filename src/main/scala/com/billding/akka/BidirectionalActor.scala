package com.billding.akka

import com.billding.kafka.{BidirectionalKafka, KafkaConfig, SimpleConsumer, SimpleProducer}
import com.billding.timing.TimedFunctions

abstract class BidirectionalActor(input: String, output: String) extends ChattyActor {
  val kafkaProps = new KafkaConfig()
  val bidirectionalKafka: BidirectionalKafka =
    new BidirectionalKafka(input, output)

  val timedFunctions: TimedFunctions = new TimedFunctions(kafkaProps.clock)
}

abstract class ConsumingActor(input: String) extends ChattyActor {
  val kafkaProps = new KafkaConfig()
  val consumer = new SimpleConsumer(input)
}

abstract class ProducingActor(output: String) extends ChattyActor {
  val kafkaProps = new KafkaConfig()
  val producer = new SimpleProducer(output)
}
