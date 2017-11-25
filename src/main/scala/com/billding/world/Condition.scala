package com.billding.world

sealed case class Condition(name: String)

object Condition {
  val clear = Condition("clear")
  val snowedOver = Condition("SnowedOver")
  val icedOver = Condition("IcedOver")
  val scalding = Condition("Scalding")

  val values = List(
    clear,
    snowedOver
  )

}
