package com.billding.weather

import java.time.Instant

import play.api.libs.json.Json

case class Condition(location: Location, weatherType: WeatherType, time: Instant)

object Condition {
  implicit val conditionFormat = Json.format[Condition]
}
