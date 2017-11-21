package com.billding.akka

import java.time.Instant

import com.billding.akka.RawWeatherAlerter.{PING, SNOW_ALERT}
import com.billding.kafka.KafkaConfigPermanent
import org.apache.kafka.clients.consumer.{ConsumerRecord, ConsumerRecords}

import scala.collection.JavaConverters._

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

class RawWeatherAlerter
  extends BidirectionalActor(
    KafkaConfigPermanent.RAW_WEATHER,
    KafkaConfigPermanent.NULL_TOPIC
  ) {
  val name = "Raw Weath Alerter"


  def specificReceive: PartialFunction[Any, Unit] = {
    case PING(startTime) =>
      println("RawWeatherAlerter got a ping")

//      bidirectionalKafka.consumer.poll(1)
//      bidirectionalKafka.consumer.seek(
//        new TopicPartition(KafkaConfigPermanent.RAW_WEATHER, 0),
//        startTime.getEpochSecond
//      )

//      bidirectionalKafka.consumer.poll(1)
//      bidirectionalKafka.consumer.seekToBeginning(
//        List(
//          new TopicPartition(KafkaConfigPermanent.RAW_WEATHER, 0)
//        )
//      )

      val records: ConsumerRecords[String, String] = bidirectionalKafka.poll(200)
      if ( records.isEmpty ) {
        println("going to try again for more records in a bit! Without blocking!")
        context.system.scheduler.scheduleOnce(
          5 milliseconds,
          self,
          PING(startTime)
        )

      } else {
        for (record: ConsumerRecord[String, String] <- records.asScala) {
          println("Actually got RAW_WEATHER record: " + record.value)
          if (record.value.contains("Snow")) {
            println("recognized snow")
            context.parent ! SNOW_ALERT("Snow coming!", startTime)
          }
        }
      }
//      pollWith( record =>{
//        println("Actually got RAW_WEATHER record: " + record.value)
//        if (record.value.contains("Snow")) {
//          println("recognized snow")
//          sender() ! SNOW_ALERT("Snow coming!", startTime)
//        }
//      }
//      )
  }
}

object RawWeatherAlerter {
  sealed  trait Actions
  case class PING(startTime: Instant) extends Actions
  case class SNOW_ALERT(msg: String, time: Instant) extends Actions
}

