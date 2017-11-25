package com.billding.akka

import java.time.{Clock, Instant}

import akka.actor.Actor
import com.billding.akka.PlowingService.{Plow, ReturnTruck}
import com.billding.weather.Location

trait Receipt {

}

case class WorkReceipt(location: Location, time: Instant) extends Receipt {

}

class PlowingService extends Actor {
  val clock = Clock.systemUTC()

  val fleetSize = 10
  var trucksAvailable: Int = fleetSize

  override def receive = {
    case plow: Plow => {
      if ( trucksAvailable > 0 ) {
        trucksAvailable -= 1
        // TODO: Alter world in some way? Add truck, mark location as busy, etc
        context.parent ! WorkReceipt(plow.location, clock.instant() )
      } else {
        // Failure receipt? Nothing at all?
        // I feel like the caller needs to know if the PlowingService as failed them.

      }

    }
    case returnTruck: ReturnTruck => {
      if ( trucksAvailable < fleetSize )
        trucksAvailable += 1
      else
        throw new RuntimeException("Illegal action! Where is that truck from?!")
    }
  }
}

object PlowingService {
  sealed trait Actions
  case class Plow(location: Location)
  case class ReturnTruck(instant: Instant)
}
