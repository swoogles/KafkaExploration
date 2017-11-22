package com.billding.akka

import com.billding.akka.DutyAlerter.PING
import com.billding.akka.RawWeatherAlerter.SNOW_ALERT
import com.billding.kafka.KafkaConfigPermanent
import com.billding.weather.WeatherType.Snow

class DutyAlerter
  extends BidirectionalActor(
    KafkaConfigPermanent.RAW_WEATHER,
    KafkaConfigPermanent.BUSINESS_TOPIC
  ) {
  val name = "Duty Alerter"

  def receive: PartialFunction[Any, Unit] = {
    case PING =>
      pollWith( record =>{
        println("got RAW_WEATHER records for duties")
        if (record.value().contains(Snow.name)) {
          bidirectionalKafka.send(
            "key",
            s"Snow is coming! Buy a coat!"
          )
        }
      }
      )

    case snowAlert: SNOW_ALERT => {
      bidirectionalKafka.send(
        "key",
        snowAlert.toString
      )
    }
  }

}

object DutyAlerter {
  sealed  trait Actions
  object PING extends Actions
}
