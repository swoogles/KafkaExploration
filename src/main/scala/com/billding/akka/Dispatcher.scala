package com.billding.akka

import java.time.Instant

import akka.actor.{Actor, ActorRef, PoisonPill, Props}
import com.billding.akka.Dispatcher.Initiate
import com.billding.akka.RawWeatherAlerter.SNOW_ALERT
import com.billding.akka.Reaper.{WatchMe, WatchUs}

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

class Dispatcher extends Actor {
  val reaper = context.actorOf(Props[ProductionReaper], name = "reaper")

  val rawWeatherActor = context.actorOf(Props[RawWeatherProducer], name = "rawWeatherProducer")
  val rawWeatherAlerter = context.actorOf(Props[RawWeatherAlerter], name = "rawWeatherAlerterActor")
  val funActor = context.actorOf(Props[FunActor], name = "funActor")
  val dutyAlerterActor = context.actorOf(Props[DutyAlerter], name = "dutyAlerterActor")

  reaper ! WatchUs(
    Seq(
      rawWeatherActor,
      rawWeatherAlerter,
      funActor,
      dutyAlerterActor
    )
  )

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

      poisonAfterPeriod(
        Seq(
          dutyAlerterActor,
          rawWeatherActor,
          rawWeatherAlerter,
          funActor
        )
      )
    }
    case snowAlert: SNOW_ALERT => {
      funActor ! snowAlert
      dutyAlerterActor ! snowAlert
    }
    case other => {
      println("unrecognized message in dispatcher")
      println(other)
    }


  }

  def poisonAfterPeriod(
    actors: Seq[ActorRef]
  ) =
    for ( actor <- actors) {
      context.system.scheduler.scheduleOnce(
        5500 milliseconds,
        actor,
        PoisonPill
      )
    }

}


object Dispatcher {
  sealed trait Actions
  case object Initiate extends Actions
}
