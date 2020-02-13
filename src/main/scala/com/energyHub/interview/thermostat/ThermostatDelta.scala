package com.energyHub.interview.thermostat

import java.time.LocalDateTime

trait WarmedData {
  def warmed(): Boolean
}

trait DeltaState[T] {
  def copyToNewState(nextState: T): T
}


case class SetTemperature(heatTemp: Option[Int], coolTemp: Option[Int]) extends WarmedData
  with DeltaState[SetTemperature] {

  override def warmed(): Boolean = heatTemp.nonEmpty && coolTemp.nonEmpty

  override def copyToNewState(nextState: SetTemperature): SetTemperature = {
    this.copy(
      heatTemp = nextState.heatTemp.orElse(this.heatTemp),
      coolTemp = nextState.coolTemp.orElse(this.coolTemp),
    )
  }

}


case class ThermostatData(lastAlertTs: Option[LocalDateTime] = None,
                          ambientTemp: Option[Int] = None,
                          schedule: Option[Boolean] = None,
                          setpoint: Option[SetTemperature] = None,
                          eventTime: Option[LocalDateTime] = None) extends WarmedData
  with DeltaState[ThermostatData] {

  override def warmed(): Boolean = {
    lastAlertTs.nonEmpty &&
      ambientTemp.nonEmpty &&
      schedule.nonEmpty &&
      setpoint.map(_.warmed()).nonEmpty
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

case class ThermostatDelta(changeTime: LocalDateTime, before: ThermostatData, after: ThermostatData)
