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

  var weatherCyclesConsumed = 0

  def receive: PartialFunction[Any, Unit] = {
    case PING(startTime) =>
      println("RawWeatherAlerter got a ping")

      val records: ConsumerRecords[String, String] = bidirectionalKafka.poll(200)
      if ( records.isEmpty ) {
        context.system.scheduler.scheduleOnce(
          5 milliseconds,
          self,
          PING(startTime)
        )

      } else {
        // This could process mid-cycle, and not actually indicate all conditions were handled.
        weatherCyclesConsumed += 1

        // Original way
        /*
        for (record: ConsumerRecord[String, String] <- records.asScala) {
          if (record.value.contains("Snow")) {
            println("recognized snow")
            context.parent ! SNOW_ALERT("Snow coming!", startTime)
          }
        }
        */

        // More functional way
        records.asScala
          .filter(_.value.contains("Snow"))
          .foreach(_ => context.parent ! SNOW_ALERT("Snow coming!", startTime))


        if ( weatherCyclesConsumed < 5 ) {
          context.system.scheduler.scheduleOnce(
            5 milliseconds,
            self,
            PING(startTime)
          )

        }
      }
  }
}

object RawWeatherAlerter {
  sealed  trait Actions
  case class PING(startTime: Instant) extends Actions
  case class SNOW_ALERT(msg: String, time: Instant) extends Actions
}

