package com.billding.akka

import com.billding.kafka.{BidirectionalKafka, KafkaConfig}
import com.billding.timing.TimedFunctions

abstract class BidirectionalActor(input: String, output: String) extends ChattyActor {
  val kafkaProps = new KafkaConfig()
  val bidirectionalKafka: BidirectionalKafka =
    new BidirectionalKafka(input, output)

  val timedFunctions: TimedFunctions = new TimedFunctions(kafkaProps.clock)
}
