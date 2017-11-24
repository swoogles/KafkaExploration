package com.billding.weather

import java.time.Clock

class ExampleScenarios(clock: Clock) {

  import WeatherType._
  val weatherPattern =
    List(
      Snow,
      Clear,
      Snow,
      Clear,
      Snow
    )

  def mostlySnow(): List[Condition] =
    for (
      (weather, idx) <- weatherPattern.zipWithIndex
    ) yield {
      Condition(Location.crestedButte, weather, clock.instant().plusSeconds(idx))
    }


}
