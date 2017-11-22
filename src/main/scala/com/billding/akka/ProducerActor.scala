package com.billding.akka

import java.time.Clock

import com.billding.weather.{Condition, Location, WeatherType}
import com.billding.kafka.{BidirectionalKafka, KafkaConfig, KafkaConfigPermanent}

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

class RawWeatherProducer
  extends BidirectionalActor(
    KafkaConfigPermanent.NULL_TOPIC,
    KafkaConfigPermanent.RAW_WEATHER
  ) {
  val name = "Raw Weath Actor"

  val clock = Clock.systemUTC()

  def weatherCycle(): List[String] = {
    for (
      location <- Location.values;
      weather <- WeatherType.values
    ) yield {
      Condition(location, weather, clock.instant().plusSeconds(weather.idx))
      clock.instant().plusSeconds(weather.idx)+ ": " + location + ": " + weather.name
    }
  }

  def receive: PartialFunction[Any, Unit] = {
    case RawWeatherProducer.START_PRODUCING_WEATHER => {
      weatherCycle().foreach(
        bidirectionalKafka.send( "key", _ )
      )
    }

    case RawWeatherProducer.WeatherCycles(count) => {
      if (count > 0) {
        weatherCycle().foreach(
          bidirectionalKafka.send("key", _)
        )
        context.system.scheduler.scheduleOnce(
          5 milliseconds,
          self,
          RawWeatherProducer.WeatherCycles(count - 1)
        )
      }
    }
  }
}

object RawWeatherProducer {
  sealed  trait Actions
  object START_PRODUCING_WEATHER extends Actions
  case class WeatherCycles(count: Int) extends Actions
}
