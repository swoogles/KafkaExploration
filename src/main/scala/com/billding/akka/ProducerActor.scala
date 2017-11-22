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

  def weatherCycle(): List[String] = {
    for ( weather <- WeatherCondition.values) yield {
      clock.instant().plusSeconds(weather.idx) + ": " + weather.name
    }
  }

  def specificReceive: PartialFunction[Any, Unit] = {
    case RawWeatherProducer.START_PRODUCING_WEATHER => {
      weatherCycle().foreach(
        bidirectionalKafka.send( "key", _ )
      )
    }
  }
}

object RawWeatherProducer {
  sealed  trait Actions
  object START_PRODUCING_WEATHER extends Actions
  case class WeatherCycles(count: Int) extends Actions
}
