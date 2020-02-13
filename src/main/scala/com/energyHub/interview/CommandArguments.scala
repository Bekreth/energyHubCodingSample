package com.energyHub.interview

import java.time.LocalDateTime

import com.energyHub.interview.thermostat.ThermostatData
import scopt.OParser

case class CommandConfig(targetDate: Option[LocalDateTime] = None,
                         fieldAccessors: Vector[ThermostatData => _])
class CommandArguements {

  val fields: OParser[String, Con] =

}
