package com.billding.lightbendpath

import java.time.Clock

import akka.actor.{ActorSystem, Props}
import com.billding.akka.RawWeatherActor
import com.billding.timing.TimedFunctions
import com.billding.zookeeper.ZookeeperConfig
import kafka.utils.ZkUtils

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

object LaunchPad extends  App{
  /*
      Necessary external steps:
      bin/zookeeper-server-start.sh config/zookeeper.properties
      bin/kafka-server-start.sh config/server.properties
      bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic test
      bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 3 --partitions 1 --topic my-replicated-topic
   */

  val clock: Clock  = Clock.systemUTC()
  val timedFunctions = new TimedFunctions(clock)

  override def main(args: Array[String]): Unit = {
    val zkUtils: ZkUtils = new ZookeeperConfig().zkUtils

    val topics: Seq[String] = zkUtils.getAllTopics()
    topics foreach println

    val system = ActorSystem("HelloSystem")
    val helloActor = system.actorOf(Props[RawWeatherActor], name = "helloactor")
    system.scheduler.scheduleOnce(
      50 milliseconds,
      helloActor,
      RawWeatherActor.START_PRODUCING_WEATHER
    )

//    val weatherAlerter = new WeatherAlerter()
//    weatherAlerter.run()

    val funAlerter = new FunAlerter(timedFunctions)
    funAlerter.run()

    system.terminate()
  }
}

