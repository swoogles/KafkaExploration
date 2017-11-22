package com.billding.weather

object WeatherType {
  val Clear = WeatherType(
    "Clear",
    0
  )

  val Snow = WeatherType(
    "Snow",
    1
  )

  val Windy = WeatherType(
    "Windy",
    2
  )

  val Rain = WeatherType(
    "Rain",
    3
  )

  val values = List(Clear, Snow, Rain, Windy)

}

sealed case class WeatherType(
  name: String,
  idx: Int
)

