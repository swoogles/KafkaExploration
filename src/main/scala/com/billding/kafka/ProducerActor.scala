package com.billding.kafka

import akka.actor.Actor
import org.apache.kafka.clients.producer._

class ProducerActor extends Actor {

  val kafkaProps = new KafkaConfig()

  val producer = new KafkaProducer[String, String](kafkaProps.props)

  def receive: PartialFunction[Any, Unit] = {
    case ProducerActor.GREET_OTHERS => {
      for(i <- 1 to 5){
        println("received in producer")
        val record = new ProducerRecord(kafkaProps.RAW_WEATHER, "key", s"snow")
        producer.send(record)
      }
    }
    case _ => println("huh?")
  }
}

object ProducerActor {
  val GREET_OTHERS = "greet_others"
}
