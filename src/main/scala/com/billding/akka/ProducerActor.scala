package com.billding.akka

import java.time.Clock

import com.billding.weather.{Condition, Location, WeatherType}
import com.billding.kafka.KafkaConfigPermanent
import play.api.libs.json.Json

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

class RawWeatherProducer
  extends ProducingActor(
    KafkaConfigPermanent.RAW_WEATHER
  ) {
  val name = "Raw Weath Actor"

  val clock = Clock.systemUTC()

  def weatherCycle(): List[Condition] =
    for (
      location <- Location.values;
      weather <- WeatherType.values
    ) yield {
      Condition(location, weather, clock.instant().plusSeconds(weather.idx))
    }

  def receive: PartialFunction[Any, Unit] = {
    case RawWeatherProducer.START_PRODUCING_WEATHER => {
      weatherCycle().foreach( condition =>
        producer.send( "key", Json.toJson(condition) )
      )
    }

    case RawWeatherProducer.WeatherCycles(count) => {
      if (count > 0) {
        weatherCycle().foreach( condition =>
          producer.send( "key", Json.toJson(condition) )
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
