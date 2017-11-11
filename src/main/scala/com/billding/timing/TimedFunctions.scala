package com.billding.timing

import java.time.{Clock, Duration}

class TimedFunctions(clock: Clock) {

  def doForPeriodOfTime(runTime: Duration, taskName: String,  func: () =>Unit ): Unit = {
    println(s"starting $taskName")
    val start = clock.instant()
    while(Duration.between(start, clock.instant()).compareTo(runTime) < 0 ) {
      func.apply()
    }
    println(s"finished $taskName")
  }

}
