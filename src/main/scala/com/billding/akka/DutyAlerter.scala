package com.billding.akka

import com.billding.akka.PlowingService.Plow
import com.billding.akka.RawWeatherAlerter.SNOW_ALERT
import com.billding.kafka.KafkaConfigPermanent
import com.billding.weather.{Condition, WeatherType}
import play.api.libs.json.Json

class DutyAlerter
  extends ProducingActor(
    KafkaConfigPermanent.BUSINESS_TOPIC
  ) {
  val name = "Duty Alerter"

  var continuousSnowDays = 0

  def receive: PartialFunction[Any, Unit] = {
    case condition: Condition => {
      println("condition: " + condition)
      if ( condition.weatherType.equals(WeatherType.Snow)) {
        // TODO I think I should be passing the PlowingService actorRef to this class's constructor.
        context.parent ! Plow(condition.location)
        continuousSnowDays += 1
      }
      else
        continuousSnowDays = 0

      println("continuousSnowDays: " + continuousSnowDays )

      if (continuousSnowDays >= 3) {
        producer.send(
          "key",
          Json.obj("alert"->s"Too many days of snow. Cancel school.")
        )

      }

    }
    case _: SNOW_ALERT => {
      producer.send(
        "key",
          Json.obj("alert"->s"Snow is coming! Buy a coat!")
      )
    }
  }

}

