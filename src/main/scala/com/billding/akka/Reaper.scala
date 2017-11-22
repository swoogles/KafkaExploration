package com.billding.akka

import akka.actor.{Actor, ActorRef, PoisonPill, Terminated}

import scala.collection.mutable.ArrayBuffer

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

object Reaper {
  // Used by others to register an Actor for watching
  case class WatchMe(ref: ActorRef)
  case class WatchUsAndPoisonAfter(refs: Traversable[ActorRef], duration: FiniteDuration)
}

abstract class Reaper extends Actor {
  import Reaper._

  // Keep track of what we're watching
  val watched = ArrayBuffer.empty[ActorRef]

  // Derivations need to implement this method.  It's the
  // hook that's called when everything's dead
  def allSoulsReaped(): Unit

  // Watch and check for termination
  final def receive = {
    case WatchMe(ref) =>
      context.watch(ref)
      watched += ref
    case WatchUsAndPoisonAfter(refs, lifespan) =>
      for (ref <- refs) {
        context.watch(ref)
        watched += ref

        poisonAfterPeriod(ref, lifespan)
      }
    case Terminated(ref) =>
      watched -= ref
      if (watched.isEmpty) allSoulsReaped()
  }

  private def poisonAfterPeriod(
    actor: ActorRef,
    lifespan: FiniteDuration
  ) =
    context.system.scheduler.scheduleOnce(
      lifespan,
      actor,
      PoisonPill
    )
}
