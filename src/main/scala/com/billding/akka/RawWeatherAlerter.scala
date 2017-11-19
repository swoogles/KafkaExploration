package com.billding.akka

import java.time.Instant

import akka.actor.ActorRef
import com.billding.akka.RawWeatherAlerter.{PING, SNOW_ALERT}
import com.billding.weather.WeatherCondition
import com.billding.kafka.{BidirectionalKafka, KafkaConfig, KafkaConfigPermanent}
import org.apache.kafka.common.TopicPartition

//import java.util.Collection
import scala.collection.JavaConversions._


class RawWeatherAlerter
  extends BidirectionalActor(
    KafkaConfigPermanent.RAW_WEATHER,
    KafkaConfigPermanent.NULL_TOPIC
  ) {
  val name = "Raw Weath Alerter"


  def specificReceive: PartialFunction[Any, Unit] = {
    case PING(starTime) =>
      println("RawWeatherAlerter got a ping")

//      bidirectionalKafka.consumer.poll(1)
//      bidirectionalKafka.consumer.seek(
//        new TopicPartition(KafkaConfigPermanent.RAW_WEATHER, 0),
//        starTime.getEpochSecond
//      )

//      bidirectionalKafka.consumer.poll(1)
//      bidirectionalKafka.consumer.seekToBeginning(
//        List(
//          new TopicPartition(KafkaConfigPermanent.RAW_WEATHER, 0)
//        )
//      )

      pollWith( record =>{
        println("Actually got RAW_WEATHER record: " + record.value)
        if (record.value.contains("Snow")) {
          println("recognized snow")
          sender() ! SNOW_ALERT("Snow coming!")
//          context.child("funActor").get.tell(SNOW_ALERT(s"Snow is coming! Get ready to shred!"), self)
//          context.actorSelection("funActor").tell(SNOW_ALERT(s"Snow is coming! Get ready to shred!"), self)
//          bidirectionalKafka.send(
//            "key",
//            s"Snow is coming! Get ready to shred!"
//          )
        }
      }
      )
  }
}

object RawWeatherAlerter {
  sealed  trait Actions
  case class PING(starTime: Instant) extends Actions
  case class SNOW_ALERT(msg: String) extends Actions
}

