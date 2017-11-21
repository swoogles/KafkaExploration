package com.billding.akka

import java.time.Clock

import com.billding.weather.WeatherCondition
import com.billding.kafka.{BidirectionalKafka, KafkaConfig, KafkaConfigPermanent}

class RawWeatherProducer
  extends BidirectionalActor(
    KafkaConfigPermanent.NULL_TOPIC,
    KafkaConfigPermanent.RAW_WEATHER
  ) {
  val name = "Raw Weath Actor"

  val clock = Clock.systemUTC()

  def specificReceive: PartialFunction[Any, Unit] = {
    case RawWeatherProducer.START_PRODUCING_WEATHER => {
      for ( weather <- WeatherCondition.values) {
        println("producing weather: "+ weather.name)
        // Should the "key" here also be locked down in some way?
        bidirectionalKafka.send(
          "key",
          clock.instant().plusSeconds(weather.idx) + ": " + weather.name
        )
      }
    }
  }
}

object RawWeatherProducer {
  sealed  trait Actions
  object START_PRODUCING_WEATHER extends Actions
}
