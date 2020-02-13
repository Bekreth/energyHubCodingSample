package com.energyHub.interview.thermostat

import java.time.LocalDateTime

trait DeltaState[T] {
  def copyToNewState(nextState: T): T
}


case class ThermostatDelta(changeTime: LocalDateTime, before: ThermostatData, after: ThermostatData)

case class SetTemperature(heatTemp: Option[Double], coolTemp: Option[Double]) extends DeltaState[SetTemperature] {

  def warmed(): Boolean = {
    heatTemp.nonEmpty &&
      coolTemp.nonEmpty
  }

  override def copyToNewState(nextState: SetTemperature): SetTemperature = {
    this.copy(
      heatTemp = nextState.heatTemp.orElse(this.heatTemp),
      coolTemp = nextState.coolTemp.orElse(this.coolTemp),
    )
  }
}


case class ThermostatData(lastAlertTs: Option[LocalDateTime] = None,
                          mode: Option[Mode] = None,
                          ambientTemp: Option[Double] = None,
                          schedule: Option[Boolean] = None,
                          setpoint: Option[SetTemperature] = None,
                          eventTime: Option[LocalDateTime] = None) extends DeltaState[ThermostatData] {
  def warmed(): Boolean = {
    lastAlertTs.nonEmpty &&
      ambientTemp.nonEmpty &&
      schedule.nonEmpty &&
      setpoint.nonEmpty &&
      setpoint.get.warmed()
  }

  override def copyToNewState(nextState: ThermostatData): ThermostatData = {
    this.copy(
      lastAlertTs = nextState.lastAlertTs.orElse(this.lastAlertTs),
      ambientTemp = nextState.ambientTemp.orElse(this.ambientTemp),
      schedule = nextState.schedule.orElse(this.schedule),
      setpoint = nextState.setpoint.map(sp => sp.copyToNewState(sp)).orElse(this.setpoint)
    )
  }
}

