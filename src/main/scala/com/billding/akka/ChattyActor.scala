package com.billding.akka

import akka.actor.Actor

abstract class ChattyActor extends  Actor {
  val name: String
  def specificReceive: PartialFunction[Any, Unit]

  def receive: PartialFunction[Any, Unit] = {
      case msg => {
        try {
        println(name + ": Start Responding")
        specificReceive(msg)
        println(name + ": Stop Responding")
        } catch {
          case ex: MatchError => println("Unexpected message for this actor: " + msg)
        }
      }
  }

}
