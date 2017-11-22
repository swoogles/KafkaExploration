package com.billding.akka

import com.billding.akka.RawWeatherAlerter.SNOW_ALERT
import com.billding.kafka.KafkaConfigPermanent
import play.api.libs.json.{JsObject, Json}

class FunActor
  extends BidirectionalActor(
    KafkaConfigPermanent.NULL_TOPIC,
    KafkaConfigPermanent.PLEASURE_TOPIC
  ) {
  val name = "Fun Actor"

  var snowCount = 0

  def receive: PartialFunction[Any, Unit] = {
    case snowAlert: SNOW_ALERT => {
      snowCount+=1
      if ( snowCount > 2 ) {
        bidirectionalKafka.send(
          "key",
          Json.obj("alert"->snowAlert.toString)
        )
      }
    }
  }

  def specificReceiveCounting: PartialFunction[Any, Unit] =
    {
      case snowAlert: SNOW_ALERT => {
        if (snowCount > 3) {
          bidirectionalKafka.send(
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
