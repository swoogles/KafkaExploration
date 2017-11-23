package com.billding.weather

import play.api.libs.json.Json

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

  implicit val weatherTypeFormat = Json.format[WeatherType]
}

sealed case class WeatherType(
  name: String,
  idx: Int
)

