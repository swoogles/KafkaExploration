package com.billding.weather

object WeatherCondition {
  val values = List(Rain, Tornado, Snow)

  object Tornado extends WeatherCondition {
    val name = "Tornado"
    val idx = 0
  }

  object Rain extends WeatherCondition {
    val name = "Rain"
    val idx = 1
  }

  object Snow extends WeatherCondition {
    val name = "Snow"
    val idx = 2
  }


}

sealed trait WeatherCondition {
  val name: String
  val idx: Int
}

