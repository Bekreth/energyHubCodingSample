package com.energyHub.interview.thermostat.deserializer

import com.energyHub.interview.thermostat.ThermostatDelta

//TODO: Generalized interface for creating various deserializers.  I had imagined making this Parquet friendly
trait ThermostatDeserializer[T] {
  def deserialize(serializedData: T): Option[ThermostatDelta]
}
