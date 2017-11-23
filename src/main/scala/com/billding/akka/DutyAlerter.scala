package com.billding.akka

import com.billding.akka.RawWeatherAlerter.SNOW_ALERT
import com.billding.kafka.KafkaConfigPermanent
import play.api.libs.json.Json

class DutyAlerter
  extends BidirectionalActor(
    KafkaConfigPermanent.RAW_WEATHER,
    KafkaConfigPermanent.BUSINESS_TOPIC
  ) {
  val name = "Duty Alerter"

  def receive: PartialFunction[Any, Unit] = {
    case _: SNOW_ALERT => {
      bidirectionalKafka.send(
        "key",
          Json.obj("alert"->s"Snow is coming! Buy a coat!")
      )
    }
  }

}

