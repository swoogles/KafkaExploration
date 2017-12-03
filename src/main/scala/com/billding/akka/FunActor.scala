package com.billding.akka

import com.billding.akka.RawWeatherAlerter.SNOW_ALERT
import com.billding.kafka.KafkaConfigPermanent
import com.billding.weather.{Condition, WeatherType}
import play.api.libs.json.{JsObject, Json}

class FunActor
  extends ProducingActor(
    KafkaConfigPermanent.PLEASURE_TOPIC
  ) {
  val name = "Fun Actor"

  var snowCount = 0

  def receive: PartialFunction[Any, Unit] = {
    case snowAlert: SNOW_ALERT => {
      snowCount+=1
      if ( snowCount > 2 ) {
        producer.send(
          "key",
          Json.obj("alert"->snowAlert.toString)
        )
      }
    }

    case condition: Condition => {
      println("condition in fun actor: " + condition)
      if (condition.weatherType.equals(WeatherType.Snow)) {
        producer.send(
          "key",
          Json.obj("alert"-> (condition.time + ": go shred it!"))
        )
      }
    }
  }

  def specificReceiveCounting: PartialFunction[Any, Unit] =
    {
      case snowAlert: SNOW_ALERT => {
        if (snowCount > 3) {
          producer.send(
            "key",
            Json.obj("alert"->snowAlert.toString)
          )
        } else {
          // Do nothign
        }
      }
    }

}

object FunActor {
  sealed  trait Actions
  object PING extends Actions
}
