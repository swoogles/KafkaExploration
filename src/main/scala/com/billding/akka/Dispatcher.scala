package com.billding.akka

import java.time.{Clock, Instant}

import akka.actor.{Actor, ActorRef, Props}
import com.billding.akka.Dispatcher.Initiate
import com.billding.akka.PlowingService.Plow
import com.billding.akka.RawWeatherAlerter.SNOW_ALERT
import com.billding.akka.Reaper.WatchUsAndPoisonAfter
import com.billding.weather.{Condition, ExampleScenarios}

import scala.collection.immutable
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

class Dispatcher extends Actor {
  val reaper: ActorRef = context.actorOf(Props[ProductionReaper], name = "reaper")

  val clock: Clock = Clock.systemUTC()

  val rawWeatherProducer: ActorRef =
    context.actorOf(Props[RawWeatherProducer], name = "rawWeatherProducer")

  val funActor: ActorRef =
    context.actorOf(Props[FunActor], name = "funActor")

  val dutyAlerterActor: ActorRef =
    context.actorOf(Props[DutyAlerter], name = "dutyAlerterActor")


  val rawWeatherAlerter: ActorRef =
//    context.actorOf(Props[RawWeatherAlerter], name = "rawWeatherAlerterActor")
  context.actorOf(Props(classOf[RawWeatherAlerter], List(funActor, dutyAlerterActor)), name = "rawWeatherAlerterActor")

  val plowingService: ActorRef =
    context.actorOf(Props[PlowingService], name = "plowingServiceActor")

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

      val weatherResponders: Seq[ActorRef] = List(funActor, dutyAlerterActor )

      rawWeatherAlerter ! weatherResponders

      context.system.scheduler.scheduleOnce(
        100 milliseconds,
        rawWeatherAlerter,
        RawWeatherAlerter.PING(startTime)
      )

      context.system .scheduler.scheduleOnce(
        200 milliseconds,
        rawWeatherProducer,
        RawWeatherProducer.WeatherStory(scenarios.mostlySnow())
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
    // I think everything coming back through to dispatcher is an anti-pattern/smell
    case plowingServiceAction: Plow => {
      plowingService ! plowingServiceAction
    }
    case receipt: Receipt => {
      println("confirmation of work: " + receipt)
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
