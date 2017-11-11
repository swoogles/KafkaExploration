package com.billding.kafka.weather

import akka.actor.Actor
import com.billding.kafka.{BidirectionalKafka, KafkaConfig}
import org.apache.kafka.clients.producer._

class RawWeatherActor extends Actor {

  val kafkaProps = new KafkaConfig()
  val bidirectionalKafka: BidirectionalKafka =
    new BidirectionalKafka(kafkaProps.NULL_TOPIC, kafkaProps.PLEASURE_TOPIC)

  /*
    Maybe the reason that actors have signatures that can see anything, and do anything in response, is that real entities
    are always present and might interact with the world in any number of ways that you can't predict from your vantage point.
   */
  def receive: PartialFunction[Any, Unit] = {
    case RawWeatherActor.START_PRODUCING_WEATHER => {
      for ( i <- WeatherCondition.values) {
        val record = new ProducerRecord(kafkaProps.RAW_WEATHER, "key", i.name)
        bidirectionalKafka.producer.send(record)
      }
    }
    case _ => throw new RuntimeException("No idea what you want this RawWeatherActor to do.")
  }
}

object RawWeatherActor {
  sealed  trait Actions
  object START_PRODUCING_WEATHER extends Actions
}
