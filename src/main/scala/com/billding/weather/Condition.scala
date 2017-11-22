package com.billding.weather

import java.time.Instant

case class Condition(location: Location, weatherType: WeatherType, time: Instant)
