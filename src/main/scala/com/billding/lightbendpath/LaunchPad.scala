package com.billding.lightbendpath

import java.time.Clock

import akka.actor.{ActorSystem, PoisonPill, Props}
import com.billding.akka.Reaper.WatchMe
import com.billding.akka.{FunActor, ProductionReaper, RawWeatherActor, Reaper}
import com.billding.timing.TimedFunctions
import com.billding.zookeeper.ZookeeperConfig
import kafka.utils.ZkUtils

import scala.collection.immutable.Range
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

    /*
    val zkUtils: ZkUtils = new ZookeeperConfig().zkUtils
    val topics: Seq[String] = zkUtils.getAllTopics()
    topics foreach println
    */

    val system = ActorSystem("HelloSystem")

    val reaper = system.actorOf(Props[ProductionReaper], name = "reaper")

    val helloActor = system.actorOf(Props[RawWeatherActor], name = "helloactor")
    reaper ! WatchMe(helloActor)

    val funActor = system.actorOf(Props[FunActor], name = "funActor")

    reaper ! WatchMe(funActor)

    system.scheduler.scheduleOnce(
      5 milliseconds,
      funActor,
      FunActor.PING
    )

    system.scheduler.scheduleOnce(
      5 milliseconds,
      funActor,
      "TOTAL GARBAGE"
    )


    system.scheduler.scheduleOnce(
      5 milliseconds,
      helloActor,
      RawWeatherActor.START_PRODUCING_WEATHER
    )


    system.scheduler.scheduleOnce(
      50 milliseconds,
      helloActor,
      PoisonPill
    )


    system.scheduler.scheduleOnce(
      50 milliseconds,
      funActor,
      PoisonPill
    )

  }
}

