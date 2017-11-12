package com.billding.akka

import akka.actor.Actor
import com.billding.weather.WeatherCondition
import com.billding.kafka.{BidirectionalKafka, KafkaConfig}

class RawWeatherActor extends Actor {

  val kafkaProps = new KafkaConfig()
  val bidirectionalKafka: BidirectionalKafka =
    new BidirectionalKafka(kafkaProps.NULL_TOPIC, kafkaProps.RAW_WEATHER)

  /*
    Maybe the reason that actors have signatures that can see anything, and do anything in response, is that real entities
    are always present and might interact with the world in any number of ways that you can't predict from your vantage point.


    Actors are just going to act.
    No actor knows what  other actors will be available.
    They just say their lines based on the line delivered to them.
    This might also include jumping a few steps ahead, if the actor communicating with them flubbed it.
      - But I don't know that.
      - Or going back in lines, if it's important enough that you need to re-prompt them.
   */
  def receive: PartialFunction[Any, Unit] = {
    case RawWeatherActor.START_PRODUCING_WEATHER => {
      println("about to make some weather")
      for ( i <- WeatherCondition.values) {
        // Should the "key" here also be locked down in some way?
        bidirectionalKafka.send(
          "key",
          i.name
        )
      }
    }
    case _ => throw new RuntimeException("No idea what you want this RawWeatherActor to do.")
  }
}

object RawWeatherActor {
  sealed  trait Actions
  object START_PRODUCING_WEATHER extends Actions
}
