package com.billding.akka

import akka.actor.Actor

/*
  Maybe the reason that actors have signatures that can see anything, and do anything in response, is that real entities
  are always present and might interact with the world in any number of ways that you can't predict from your vantage point.


  Actors are just going to act.
  No actor knows what  other actors will be available.
  They just say their lines based on the line delivered to them.
  This might also include jumping a few steps ahead, if the actor communicating with them flubbed it.
    - But I don't know that.
    - Or going back in lines, if it's important enough that you need to re-prompt them.
 */
abstract class ChattyActor extends  Actor {
  val name: String
//  def specificReceive: PartialFunction[Any, Unit]
//
//  def receive: PartialFunction[Any, Unit] = {
//      case msg => {
//        try {
//        println(name + ": Start Responding to : " + msg)
//        specificReceive(msg)
//        println(name + ": Stop Responding to : " + msg)
//        } catch {
//          case ex: MatchError => println("Unexpected message for actor " + name + ": " + msg)
//        }
//      }
//  }

}
