package com.billding.akka

import akka.actor.PoisonPill

class ProductionReaper extends Reaper {
  // Shutdown
  def allSoulsReaped(): Unit = {
    println("reaper gonna reap")
    context.parent.tell(PoisonPill, self)
    println("poisoned my parent")
    context.system.terminate()
  }
}
