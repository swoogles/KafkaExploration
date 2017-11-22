package com.billding.weather

sealed case class Location(
  name: String
)

object Location {
  val values = List(phoenix, crestedButte, sanFrancisco)
  val phoenix = Location("Phoenix")
  val crestedButte = Location("CrestedButte")
  val sanFrancisco = Location("SanFrancisco")
//  case object CrestedButte extends Location
//  case object SanFrancisco extends Location
}
