
package com.billding.lightbendpath

import java.time.Duration

import com.billding.kafka.{BidirectionalKafka, KafkaConfig}
import com.billding.timing.TimedFunctions
import org.apache.kafka.clients.consumer.{ConsumerRecord, ConsumerRecords}

import scala.collection.JavaConverters._

class FunAlerter(timedFunctions: TimedFunctions) {
  /*
  Maybe each Actor should receive a "tick" that tells it to poll for a period.
  I think this could be done in a way that doesn't involve deadlocking processes.
   */
  val kafkaProps = new KafkaConfig()
  val bidirectionalKafka: BidirectionalKafka = new BidirectionalKafka(kafkaProps.RAW_WEATHER, kafkaProps.PLEASURE_TOPIC)

  def run() = {
    val determinePurchases: () => Unit = () => {

      val records: ConsumerRecords[String, String] = bidirectionalKafka.consumer.poll(100)
      for (record: ConsumerRecord[String, String] <- records.asScala) {
        if (record.value.contains("snow")) {
          bidirectionalKafka.send(
            "key",
            s"Snow is coming! Get ready to shred!"
          )
        }
      }
    }

    timedFunctions.doForPeriodOfTime(
      Duration.ofSeconds(1), "funAlerter"
    )(determinePurchases)
  }
}
