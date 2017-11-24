package com.billding.weather

import play.api.libs.json.{Format, Json}

object WeatherType {
  val Clear = WeatherType(
    "Clear"
  )

  val Snow = WeatherType(
    "Snow"
  )

  val Windy = WeatherType(
    "Windy"
  )

  val Rain = WeatherType(
    "Rain"
  )

  val HeatWave = WeatherType(
    "HeatWave"
  )


  val values = List(Clear, Snow, Rain, Windy)

  implicit val weatherTypeFormat: Format[WeatherType] = Json.format[WeatherType]
}

sealed case class WeatherType(
  name: String
)

