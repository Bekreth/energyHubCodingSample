package com.energyHub.interview.thermostatDeserializer

import com.energyHub.interview.ThermostatDelta

trait ThermostatDeserializer[T] {
  def deserialize(serializedData: T): Option[ThermostatDelta]
}
