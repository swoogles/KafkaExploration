package com.billding.akka

import java.time.Instant

import com.billding.akka.PlowingService.Plow
import com.billding.akka.RawWeatherAlerter.SNOW_ALERT
import com.billding.kafka.KafkaConfigPermanent
import com.billding.weather.{Condition, WeatherType}
import play.api.libs.json.Json

/* OOOO I think this actor should mutate between normal and disaster modes!!
*   If conditions get too snowy/rainy/dry it can go into Blizzard/Flood/Drought modes.
*
*/
class DutyAlerter
  extends ProducingActor(
    KafkaConfigPermanent.BUSINESS_TOPIC
  ) {
  val name = "Duty Alerter"

  case class Alert(msg: String, time: Instant)
  object Alert {
    implicit val conditionFormat = Json.format[Alert]
  }

  var continuousSnowDays = 0

  def blizzardConditions: PartialFunction[Any, Unit] = {
    case condition: Condition => {
      println("condition: " + condition)
      if (condition.weatherType.equals(WeatherType.Snow)) {
        producer.send(
          "key",
          Json.obj("alert" -> s"Send out the plow trucks. We're in blizzard mode.")
        )
        producer.send(
          "key",
          Json.toJson(Alert("Too many days of snow. Cancel school. We're in blizzard mode.", condition.time))
        )

        if ( continuousSnowDays >= 5) {
          producer.send(
            "key",
            Json.obj("alert"->s"Conditions are dire. Sound out trucks to help those stuck.")
          )
        }
      } else {
        continuousSnowDays = 0
        println("becoming normal again")
        context.become(normalConditions)
      }
    }
  }

  blizzardConditions.andThen(blizzardConditions)

  def normalConditions: PartialFunction[Any, Unit] = {
    case condition: Condition => {
      println("condition: " + condition)
      if (condition.weatherType.equals(WeatherType.Snow)) {
        continuousSnowDays += 1
        // TODO I think I should be passing the PlowingService actorRef to this class's constructor.

        producer.send(
          "key",
          Json.obj("alert" -> s"Send out the plow trucks.")
        )
        context.parent ! Plow(condition.location)
      }
      else
        continuousSnowDays = 0

      if (continuousSnowDays >= 3) {
        context.become( blizzardConditions )
      }
    }
  }

  def receive: PartialFunction[Any, Unit] = {
    normalConditions
  }

}

