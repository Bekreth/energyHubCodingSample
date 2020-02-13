package com.energyHub.interview.thermostat.deserializer
import com.energyHub.interview.thermostat.ThermostatDelta
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.scala.DefaultScalaModule

object JsonDeserializer extends ThermostatDeserializer[String] {

  private val mapper = new ObjectMapper()
  mapper.registerModule(DefaultScalaModule)
  mapper.registerModule(new JavaTimeModule())

  override def deserialize(serializedData: String): Option[ThermostatDelta] = {
    try {
      Some(mapper.readValue(serializedData, classOf[ThermostatDelta]))
    } catch {
      case exception: Exception => {
        println("Was unable to deserialize string: %s: %s", serializedData, exception)
        None
      }
    }
  }
}
