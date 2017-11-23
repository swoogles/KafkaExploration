package com.billding.akka

import java.time.Instant

import com.billding.akka.RawWeatherAlerter.{PING, SNOW_ALERT}
import com.billding.kafka.KafkaConfigPermanent
import com.billding.weather.{Condition, WeatherType}
import org.apache.kafka.clients.consumer.{ConsumerRecord, ConsumerRecords}
import play.api.libs.json.{JsValue, Json}

import scala.collection.JavaConverters._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

class RawWeatherAlerter
  extends ConsumingActor(
    KafkaConfigPermanent.RAW_WEATHER
  ) {
  val name = "Raw Weath Alerter"

  var weatherCyclesConsumed = 0

  def receive: PartialFunction[Any, Unit] = {
    case PING(startTime) =>
      val records: ConsumerRecords[String, String] = consumer.poll(200)
      if ( ! records.isEmpty ) {
        // This could process mid-cycle, and not actually indicate all conditions were handled.
        weatherCyclesConsumed += 1

        val typedRecords: Iterable[Condition] = records.asScala
          .map(record=>Json.parse(record.value).as[Condition])

        typedRecords
          .filter(condition=>condition.weatherType.equals(WeatherType.Snow))
          .foreach(_ => context.parent ! SNOW_ALERT("Snow coming!", startTime))
      }
      if ( weatherCyclesConsumed < 5 ) {
        context.system.scheduler.scheduleOnce(
          5 milliseconds,
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

