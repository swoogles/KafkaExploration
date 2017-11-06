package com.billding.kafka

import java.time.{Duration, Instant}
import java.util

import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer._

import scala.collection.JavaConverters._

object ConsumerAndForwarder {
  def run() = {

    val kafkaProps = new KafkaConfig()

    val consumer = new KafkaConsumer[String, String](kafkaProps.props)

    consumer.subscribe(util.Collections.singletonList(kafkaProps.TOPIC_1))

    val producer = new KafkaProducer[String, String](kafkaProps.props)

    val runTime = Duration.ofSeconds(5)
    val start = Instant.now()
    while(Duration.between(start, Instant.now()).compareTo(runTime) < 0 ) {
      val records = consumer.poll(100)
      for (record <- records.asScala) {
        println("consuming")
        if (record.value.contains("3")) {
          println(record)
          val record2 = new ProducerRecord(
            kafkaProps.TOPIC_2,
            "key",
            s"filtered msg: ${record.value}"
          )
          producer.send(record2)
        }
      }
    }
    println("done consuming/forwarding")
  }
}
