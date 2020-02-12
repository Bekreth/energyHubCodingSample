package com.energyHub.interview

import java.time.LocalDateTime


case class SetTemperature(heatTemp: Int, coolTemp: Int)
case class ThermostatData(lastAlertTs: LocalDateTime, ambientTemp: Int, schedule: Boolean, setpoint: SetTemperature)

case class ThermostatDelta(changeTime: LocalDateTime, before: ThermostatData, after: ThermostatData)
