package com.billding.akka

import java.time.Duration

import com.billding.kafka.{BidirectionalKafka, KafkaConfig}
import com.billding.timing.TimedFunctions
import org.apache.kafka.clients.consumer.{ConsumerRecord, ConsumerRecords}

abstract class BidirectionalActor(input: String, output: String) extends ChattyActor {
  val kafkaProps = new KafkaConfig()
  val bidirectionalKafka: BidirectionalKafka =
    new BidirectionalKafka(input, output)

  import scala.collection.JavaConverters._

  val timedFunctions: TimedFunctions = new TimedFunctions(kafkaProps.clock)


  def pollWith(pollingFunc: ConsumerRecord[String, String] => Unit) = {
    timedFunctions.doForPeriodOfTime(Duration.ofSeconds(2), "polling", () => {
      val records: ConsumerRecords[String, String] = bidirectionalKafka.poll(200)
      for (record: ConsumerRecord[String, String] <- records.asScala) {
        println("Got a record")
        pollingFunc(record)
      }
    }
    )
  }
}
