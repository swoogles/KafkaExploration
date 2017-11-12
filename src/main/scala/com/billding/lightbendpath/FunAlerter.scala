
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
  val bidirectionalKafka: BidirectionalKafka =
    new BidirectionalKafka(kafkaProps.RAW_WEATHER, kafkaProps.PLEASURE_TOPIC)

  def determinePurchases: () => Unit = () => {

    println("a")
    val records: ConsumerRecords[String, String] = bidirectionalKafka.consumer.poll(100)
    println("b")
    for (record: ConsumerRecord[String, String] <- records.asScala) {
      println("c")
      if (record.value.contains("snow")) {
        bidirectionalKafka.send(
          "key",
          s"Snow is coming! Get ready to shred!"
        )
      }
    }
  }

  def run() = {

    println("null step")
    println(determinePurchases)

//    timedFunctions.doForPeriodOfTime(
//      Duration.ofSeconds(2),
//      "funAlerter" ,
//      determinePurchases)

    determinePurchases.apply()
  }
}
