package com.energyHub.interview.thermostat.deserializer
import com.energyHub.interview.thermostat.ThermostatDelta
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.typesafe.scalalogging.LazyLogging

object JsonDeserializer extends ThermostatDeserializer[String]
  with LazyLogging {

  private val mapper = new ObjectMapper()
  mapper.registerModule(DefaultScalaModule)
  mapper.registerModule(new JavaTimeModule())

  override def deserialize(serializedData: String): Option[ThermostatDelta] = {
    try {
      Some(mapper.readValue(serializedData, classOf[ThermostatDelta]))
    } catch {
      case _ => {
        logger.warn("Unable to deserialize string: %s", serializedData)
        None
      }
    }
  }
}
