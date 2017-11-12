package com.billding.akka

class ProductionReaper extends Reaper {
  // Shutdown
  def allSoulsReaped(): Unit = {
    println("reaper gonna reap")
    context.system.terminate()
  }
}
