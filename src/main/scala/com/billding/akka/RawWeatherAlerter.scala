package com.billding.akka

import java.time.Instant

import akka.actor.ActorRef
import com.billding.akka.RawWeatherAlerter.{PING, SNOW_ALERT}
import com.billding.kafka.KafkaConfigPermanent
import com.billding.weather.{Condition, WeatherType}
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.common.TopicPartition
import play.api.libs.json.Json

import scala.collection.JavaConverters._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

class RawWeatherAlerter(downStreamActors: Seq[ActorRef])
  extends ConsumingActor(
    KafkaConfigPermanent.RAW_WEATHER
  ) {
  val name = "Raw Weath Alerter"

  var weatherCyclesConsumed = 0
  consumer.poll(200) // Clear out the line

  var recordsConsumed = 0

  def receive: PartialFunction[Any, Unit] = {
    case PING(startTime) =>
      /*
      consumer.consumer.seek(
      new TopicPartition("raw_weather_retry", 1),
        0
      )
      */
      val records: ConsumerRecords[String, String] = consumer.poll(200)
      println("numRecords: " + records.count())
      recordsConsumed += records.count()
      if ( ! records.isEmpty ) {
        // This could process mid-cycle, and not actually indicate all conditions were handled.
        weatherCyclesConsumed += 1

        val typedRecords: Iterable[Condition] = records.asScala
          .map(record=>Json.parse(record.value).as[Condition])

        typedRecords
          .foreach({
            record =>
              downStreamActors.foreach(_ ! record)
//            context.parent ! record
          })
      }
      if ( recordsConsumed < 7) {
        context.system.scheduler.scheduleOnce(
          50 milliseconds,
          self,
          PING(startTime)
        )
      }
  }
}

object RawWeatherAlerter {
  sealed  trait Actions
  case class PING(startTime: Instant) extends Actions
  case class SNOW_ALERT(msg: String, time: Instant) extends Actions
}

