package com.billding.akka

import akka.actor.Actor
import com.billding.akka.FunActor.PING
import com.billding.kafka.{BidirectionalKafka, KafkaConfig}
import org.apache.kafka.clients.consumer.{ConsumerRecord, ConsumerRecords}

import scala.collection.JavaConverters._

class FunActor extends ChattyActor {
  val name = "Fun Actor"

  val kafkaProps = new KafkaConfig()
  val bidirectionalKafka: BidirectionalKafka =
    new BidirectionalKafka(kafkaProps.RAW_WEATHER, kafkaProps.PLEASURE_TOPIC)

  def specificReceive: PartialFunction[Any, Unit] = {
    case PING => {
      val records: ConsumerRecords[String, String] = bidirectionalKafka.poll(100)
      for (record: ConsumerRecord[String, String] <- records.asScala) {
        println("Actually got RAW_WEATHER records")
        if (record.value.contains("Snow")) {
          bidirectionalKafka.send(
            "key",
            s"Snow is coming! Get ready to shred!"
          )
        }
      }
    }
  }
}

object FunActor {
  sealed  trait Actions
  object PING extends Actions
}
