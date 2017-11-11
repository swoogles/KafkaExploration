package com.billding.kafka

import akka.actor.{ActorSystem, Props}
import com.billding.kafka.weather.RawWeatherActor
import com.billding.timing.TimedFunctions

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import kafka.admin.AdminUtils
import kafka.utils.ZkUtils

object LaunchPad extends  App{
  /*
      Necessary external steps:
      bin/zookeeper-server-start.sh config/zookeeper.properties
      bin/kafka-server-start.sh config/server.properties
      bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic test
      bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 3 --partitions 1 --topic my-replicated-topic
   */

  override def main(args: Array[String]): Unit = {
    val sessionTimeoutMs = 10 * 1000
    val connectionTimeoutMs = 8 * 1000

    val zkUtils: ZkUtils = ZkUtils.apply(
      "localhost:2181",
      sessionTimeoutMs,
      connectionTimeoutMs,
      false
    )

    val topics: Seq[String] = zkUtils.getAllTopics()
    topics foreach println

    val system = ActorSystem("HelloSystem")
    val helloActor = system.actorOf(Props[RawWeatherActor], name = "helloactor")
    system.scheduler.scheduleOnce(50 milliseconds, helloActor, RawWeatherActor.GREET_OTHERS)
//    val consumerAndForwarder = new ConsumerAndForwarder()
//      consumerAndForwarder.run()

//    val weatherAlerter = new WeatherAlerter()
//    weatherAlerter.run()

    val funAlerter = new FunAlerter(new TimedFunctions)
    funAlerter.run()

    system.terminate()
  }
}

