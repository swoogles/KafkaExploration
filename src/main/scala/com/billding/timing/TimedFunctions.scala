package com.billding.timing

import java.time.{Duration, Instant}

class TimedFunctions {

  def doForPeriodOfTime(runTime: Duration, taskName: String) ( func: () =>Unit ): Unit = {
    println(s"starting $taskName")
    val start = Instant.now()
    while(Duration.between(start, Instant.now()).compareTo(runTime) < 0 ) {
      func.apply()
    }
    println(s"finished $taskName")
  }

}
