package com.billding.weather

import java.time.{Clock, Duration}

class ExampleScenarios(clock: Clock) {

  import WeatherType._
  val weatherPattern =
    List(
      Snow,
      Clear,
      Snow,
      Snow,
      Snow,
      Snow,
      Clear,
      Snow,
      Snow,
      Snow,
      Snow,
      Snow,
      Clear
    )

  def mostlySnow(): List[Condition] =
    for (
      (weather, idx) <- weatherPattern.zipWithIndex
    ) yield {
      Condition(Location.crestedButte, weather, clock.instant().plus(Duration.ofDays(idx)))
//      Condition(Location.crestedButte, weather, clock.instant().plusSeconds(idx))
    }


}
