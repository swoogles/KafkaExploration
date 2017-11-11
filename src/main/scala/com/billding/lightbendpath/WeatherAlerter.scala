package com.billding.lightbendpath

import java.time.Duration

import com.billding.kafka.{BidirectionalKafka, KafkaConfig}
import com.billding.timing.TimedFunctions
import org.apache.kafka.clients.consumer.ConsumerRecords

import scala.collection.JavaConverters._

class WeatherAlerter(timedFunctions: TimedFunctions) {

  val kafkaProps = new KafkaConfig()
  val bidirectionalKafka: BidirectionalKafka =
    new BidirectionalKafka(kafkaProps.RAW_WEATHER, kafkaProps.BUSINESS_TOPIC)

  def run() = {
    val determinePurchases: () => Unit = () => {

      val records: ConsumerRecords[String, String] = bidirectionalKafka.consumer.poll(100)
      for (record <- records.asScala) {
        if (record.value().contains("snow")) {
          bidirectionalKafka.send(
            "key",
            s"Snow is coming! Buy a coat!"
          )
        }
      }
    }

    timedFunctions.doForPeriodOfTime(Duration.ofSeconds(1), "weatherAlerter")(determinePurchases)
  }
}
