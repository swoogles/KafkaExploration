package com.billding.akka

import com.billding.weather.Condition
import com.billding.kafka.KafkaConfigPermanent
import play.api.libs.json.Json

class RawWeatherProducer
  extends ProducingActor(
    KafkaConfigPermanent.RAW_WEATHER
  ) {
  val name = "Raw Weath Actor"

  def receive: PartialFunction[Any, Unit] = {
    case RawWeatherProducer.START_PRODUCING_WEATHER => {
      // Come up with chaotic weather here? Probably not. I think it belongs elsewhere
    }

    case RawWeatherProducer.WeatherStory(conditions) => {
      conditions.foreach( condition =>
        producer.send( "key", Json.toJson(condition) )
      )
    }

  }
}

object RawWeatherProducer {
  sealed  trait Actions
  object START_PRODUCING_WEATHER extends Actions
  case class WeatherStory(conditions: Iterable[Condition]) extends Actions
}
