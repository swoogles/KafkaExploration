package com.billding.akka

import com.billding.akka.FunActor.PING
import com.billding.kafka.{KafkaConfigPermanent}

class FunActor
  extends BidirectionalActor(
    KafkaConfigPermanent.RAW_WEATHER,
    KafkaConfigPermanent.PLEASURE_TOPIC
  ) {
  val name = "Fun Actor"

  def specificReceive: PartialFunction[Any, Unit] = {
    case PING =>
      pollWith( record =>{
        println("Actually got RAW_WEATHER records")
        if (record.value.contains("Snow")) {
          bidirectionalKafka.send(
            "key",
            s"Snow is coming! Get ready to shred!"
          )
        }
      }
      )
  }

}

object FunActor {
  sealed  trait Actions
  object PING extends Actions
}
