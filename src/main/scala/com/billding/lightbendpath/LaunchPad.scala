package com.billding.lightbendpath

import java.time.Clock

import akka.actor.{ActorSystem, PoisonPill, Props}
import com.billding.akka.Dispatcher.Initiate
import com.billding.akka.Reaper.WatchMe
import com.billding.akka.{Dispatcher, DutyAlerter, FunActor, ProductionReaper, RawWeatherAlerter, RawWeatherProducer, Reaper}
import com.billding.timing.TimedFunctions
import com.billding.weather.{Condition, Location, WeatherType}

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

    import play.api.libs.json._

    implicit val locationWrites = Json.writes[Location]
    val location = Location.crestedButte

    implicit val weatherTypeWrites = Json.writes[WeatherType]
    val weatherType = WeatherType.Snow

    implicit val conditionWrites = Json.writes[Condition]
    val clock = Clock.systemUTC()
    val condition = Condition(location, weatherType, clock.instant())
    val json: JsValue = Json.toJson(condition)
    println(Json.toJson(condition))

    val system = ActorSystem("HelloSystem")

    val dispatcher = system.actorOf(Props[Dispatcher], name = "dispatcher")

    system.scheduler.scheduleOnce(
      15 milliseconds,
      dispatcher,
      Initiate
    )



    // D'OH! This was killing me, regardless of the niftier stuff I was trying later!
//    system.scheduler.scheduleOnce(
//      5550 milliseconds,
//      dispatcher,
//      PoisonPill
//    )

  }
}

