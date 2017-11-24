package com.billding.akka

import java.time.{Clock, Instant}

import akka.actor.{Actor, Props}
import com.billding.akka.Dispatcher.Initiate
import com.billding.akka.RawWeatherAlerter.SNOW_ALERT
import com.billding.akka.Reaper.WatchUsAndPoisonAfter
import com.billding.weather.{Condition, ExampleScenarios}

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

class Dispatcher extends Actor {
  val reaper = context.actorOf(Props[ProductionReaper], name = "reaper")

  val clock = Clock.systemUTC()

  val rawWeatherProducer =
    context.actorOf(Props[RawWeatherProducer], name = "rawWeatherProducer")
  val rawWeatherAlerter =
    context.actorOf(Props[RawWeatherAlerter], name = "rawWeatherAlerterActor")
  val funActor =
    context.actorOf(Props[FunActor], name = "funActor")
  val dutyAlerterActor =
    context.actorOf(Props[DutyAlerter], name = "dutyAlerterActor")

  reaper ! WatchUsAndPoisonAfter(
    Seq(
      rawWeatherProducer,
      rawWeatherAlerter,
      funActor,
      dutyAlerterActor
    ),
    5500 milliseconds
  )

  override def receive = {
    case Initiate => {
      println("start doing stuff!")
      // TODO remove nasty clock side-effects
      val startTime = clock.instant().minusSeconds(10)
      val scenarios = new ExampleScenarios(clock)

      println("Going to start scenario with " + scenarios.mostlySnow().length + " items")

      context.system .scheduler.scheduleOnce(
        2001 milliseconds,
        rawWeatherProducer,
        RawWeatherProducer.WeatherStory(scenarios.mostlySnow())
      )

      context.system.scheduler.scheduleOnce(
        100 milliseconds,
        rawWeatherAlerter,
        RawWeatherAlerter.PING(startTime)
      )

    }
    case snowAlert: SNOW_ALERT => {
      funActor ! snowAlert
      dutyAlerterActor ! snowAlert
    }
    case condition: Condition => {
//      funActor ! condition
      dutyAlerterActor ! condition

    }
    case other => {
      println("unrecognized message in dispatcher")
      println(other)
    }


  }

}

object Dispatcher {
  sealed trait Actions
  case object Initiate extends Actions
}
