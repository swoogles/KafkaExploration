package com.billding.akka

import java.time.Instant

import akka.actor.{Actor, PoisonPill, Props}
import com.billding.akka.Dispatcher.Initiate
import com.billding.akka.RawWeatherAlerter.SNOW_ALERT
import com.billding.akka.Reaper.WatchMe

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

class Dispatcher extends Actor {
  val reaper = context.actorOf(Props[ProductionReaper], name = "reaper")

  val rawWeatherActor = context.actorOf(Props[RawWeatherProducer], name = "rawWeatherProducer")
  reaper ! WatchMe(rawWeatherActor)

  val rawWeatherAlerter = context.actorOf(Props[RawWeatherAlerter], name = "rawWeatherAlerterActor")
  reaper ! WatchMe(rawWeatherAlerter)

  val funActor = context.actorOf(Props[FunActor], name = "funActor")
  reaper ! WatchMe(funActor)
  override def receive = {
    case Initiate => {
      println("start doing stuff!")
      val startTime = Instant.now().minusSeconds(10)

      context.system .scheduler.scheduleOnce(
        2001 milliseconds,
        rawWeatherActor,
        RawWeatherProducer.START_PRODUCING_WEATHER
      )

      context.system.scheduler.scheduleOnce(
        100 milliseconds,
        rawWeatherAlerter,
        RawWeatherAlerter.PING(startTime)
      )

      context.system.scheduler.scheduleOnce(
        5500 milliseconds,
        rawWeatherActor,
        PoisonPill
      )

      context.system.scheduler.scheduleOnce(
        5500 milliseconds,
        rawWeatherAlerter,
        PoisonPill
      )

      context.system.scheduler.scheduleOnce(
        5550 milliseconds,
        funActor,
        PoisonPill
      )

    }
    case SNOW_ALERT(msg, time) => {
      funActor ! SNOW_ALERT(msg, time)
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
