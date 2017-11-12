package com.billding.lightbendpath

import java.time.Clock

import akka.actor.{ActorSystem, PoisonPill, Props}
import com.billding.akka.Reaper.WatchMe
import com.billding.akka.{DutyAlerter, FunActor, ProductionReaper, RawWeatherActor, Reaper}
import com.billding.timing.TimedFunctions

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
    import com.billding.zookeeper.ZookeeperConfig
    import kafka.utils.ZkUtils
    val zkUtils: ZkUtils = new ZookeeperConfig().zkUtils
    val topics: Seq[String] = zkUtils.getAllTopics()
    topics foreach println
    */

    val system = ActorSystem("HelloSystem")

    val reaper = system.actorOf(Props[ProductionReaper], name = "reaper")

    val rawWeatherActor = system.actorOf(Props[RawWeatherActor], name = "helloactor")
    reaper ! WatchMe(rawWeatherActor)

    val funActor = system.actorOf(Props[FunActor], name = "funActor")
    reaper ! WatchMe(funActor)

    val dutyActor = system.actorOf(Props[DutyAlerter], name = "dutyActor")

    reaper ! WatchMe(dutyActor)

    system.scheduler.scheduleOnce(
      5 milliseconds,
      funActor,
      FunActor.PING
    )

    system.scheduler.scheduleOnce(
      5 milliseconds,
      dutyActor,
      DutyAlerter.PING
    )


    system.scheduler.scheduleOnce(
      15 milliseconds,
      rawWeatherActor,
      RawWeatherActor.START_PRODUCING_WEATHER
    )

    system.scheduler.scheduleOnce(
      150 milliseconds,
      rawWeatherActor,
      PoisonPill
    )


    system.scheduler.scheduleOnce(
      150 milliseconds,
      funActor,
      PoisonPill
    )

    system.scheduler.scheduleOnce(
      150 milliseconds,
      dutyActor,
      PoisonPill
    )

  }
}

