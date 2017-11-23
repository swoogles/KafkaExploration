package com.billding.weather

import play.api.libs.json.Json

sealed case class Location(
  name: String
)

object Location {
  val phoenix = Location("Phoenix")
  val crestedButte = Location("CrestedButte")
  val sanFrancisco = Location("SanFrancisco")
  val values = List(phoenix, crestedButte, sanFrancisco)

  implicit val locationFormat = Json.format[Location]
}
