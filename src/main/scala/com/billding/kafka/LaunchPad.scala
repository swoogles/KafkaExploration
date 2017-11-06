package com.billding.kafka

import akka.actor.{ActorSystem, Props}

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
      false)

    val topics: Seq[String] = zkUtils.getAllTopics()
    topics foreach println

    println("Hi!")
    val system = ActorSystem("HelloSystem")
    // default Actor constructor
    val helloActor = system.actorOf(Props[ProducerActor], name = "helloactor")
//    system.scheduler.schedule(50 milliseconds, 1000 milliseconds, helloActor, "kafka")
    system.scheduler.scheduleOnce(50 milliseconds, helloActor, ProducerActor.GREET_OTHERS)
    ConsumerAndForwarder.run()
    system.terminate()
  }
}

