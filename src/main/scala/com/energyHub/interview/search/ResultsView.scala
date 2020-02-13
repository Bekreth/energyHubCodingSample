package com.energyHub.interview.search

import java.time.LocalDateTime

import com.energyHub.interview.thermostat.{Mode, ThermostatData}
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.scala.DefaultScalaModule

case class ResultsView(eventTime: LocalDateTime, lastAlertTs: LocalDateTime,
                       ambientTemp: Double, heatTemp: Double, coolTemp: Double,
                       scheduled: Boolean, mode: Mode) {

  lazy val serialize: String = {
    val mapper = new ObjectMapper()
    mapper.registerModule(DefaultScalaModule)
    mapper.registerModule(new JavaTimeModule())
    mapper.writeValueAsString(this)
  }
}

object ResultsView {

  def readThermostatData[T](data: ThermostatData): ResultsView = {
    ResultsView(
      eventTime = data.eventTime.get,
      lastAlertTs = data.lastAlertTs.get,
      ambientTemp = data.ambientTemp.get,
      heatTemp = data.setpoint.get.heatTemp.get,
      coolTemp = data.setpoint.get.coolTemp.get,
      scheduled = data.schedule.get,
      mode = data.mode.orNull
    )
  }
}