package com.billding.kafka

import akka.actor.Actor
import org.apache.kafka.clients.producer._

class ProducerActor extends Actor {

  val kafkaProps = new KafkaConfig()

  val producer = new KafkaProducer[String, String](kafkaProps.props)

  val TOPIC = "test2"
  def receive: PartialFunction[Any, Unit] = {
    case ProducerActor.GREET_OTHERS => {
      for(i <- 1 to 5){
        println("received in producer")
        val record = new ProducerRecord(TOPIC, "key", s"hello from scala code $i")
        producer.send(record)
      }
    }
    case _ => println("huh?")
  }
}

object ProducerActor {
  val GREET_OTHERS = "greet_others"
}
