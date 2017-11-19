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

  val rawWeatherActor = context.actorOf(Props[RawWeatherProducer], name = "helloactor")
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
        1 milliseconds,
        rawWeatherActor,
        RawWeatherProducer.START_PRODUCING_WEATHER
      )

      context.system.scheduler.scheduleOnce(
        100 milliseconds,
        rawWeatherAlerter,
        RawWeatherAlerter.PING(startTime)
      )

      context.system.scheduler.scheduleOnce(
        1500 milliseconds,
        rawWeatherActor,
        PoisonPill
      )

      context.system.scheduler.scheduleOnce(
        1500 milliseconds,
        rawWeatherAlerter,
        PoisonPill
      )



      context.system.scheduler.scheduleOnce(
        550 milliseconds,
        funActor,
        PoisonPill
      )
    }
    case SNOW_ALERT(msg) => {
      funActor ! SNOW_ALERT(msg)
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
