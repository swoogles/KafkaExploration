package com.billding.akka

import com.billding.akka.RawWeatherAlerter.SNOW_ALERT
import com.billding.kafka.KafkaConfigPermanent

class FunActor
  extends BidirectionalActor(
    KafkaConfigPermanent.NULL_TOPIC,
    KafkaConfigPermanent.PLEASURE_TOPIC
  ) {
  val name = "Fun Actor"

  def specificReceive: PartialFunction[Any, Unit] = {
    case snowAlert: SNOW_ALERT => {
      println("got a snow alert in fun actor")

      bidirectionalKafka.send(
        "key",
        snowAlert.toString
      )
    }
  }

}

object FunActor {
  sealed  trait Actions
  object PING extends Actions
}
