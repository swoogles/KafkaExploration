package com.billding.kafka

import java.time.Duration

import com.billding.timing.TimedFunctions
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.clients.producer._

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
          val callToAction = new ProducerRecord(
            kafkaProps.BUSINESS_TOPIC,
            "key",
            s"Snow is coming! Buy a coat!"
          )
          bidirectionalKafka.producer.send(callToAction)
        }
      }
    }

    timedFunctions.doForPeriodOfTime(Duration.ofSeconds(1), "weatherAlerter")(determinePurchases)
  }
}
