package com.billding.akka

import com.billding.weather.WeatherCondition
import com.billding.kafka.{BidirectionalKafka, KafkaConfig, KafkaConfigPermanent}

class RawWeatherActor
  extends BidirectionalActor(
    KafkaConfigPermanent.NULL_TOPIC,
    KafkaConfigPermanent.RAW_WEATHER
  ) {
  val name = "Raw Weath Actor"

  def specificReceive: PartialFunction[Any, Unit] = {
    case RawWeatherActor.START_PRODUCING_WEATHER => {
      for ( weather <- WeatherCondition.values) {
        // Should the "key" here also be locked down in some way?
        bidirectionalKafka.send(
          "key",
          weather.name
        )
      }
    }
  }
}

object RawWeatherActor {
  sealed  trait Actions
  object START_PRODUCING_WEATHER extends Actions
}
