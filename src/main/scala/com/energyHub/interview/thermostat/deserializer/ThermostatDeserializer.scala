package com.energyHub.interview.thermostat.deserializer

import com.energyHub.interview.thermostat.ThermostatDelta

trait ThermostatDeserializer[T] {
  def deserialize(serializedData: T): Option[ThermostatDelta]
}
