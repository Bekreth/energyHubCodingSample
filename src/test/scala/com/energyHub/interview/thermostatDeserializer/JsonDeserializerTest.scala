package com.energyHub.interview.thermostatDeserializer

import java.time.LocalDateTime

import com.energyHub.interview.{SetTemperature, ThermostatData, ThermostatDelta}
import org.junit.runner.RunWith
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class JsonDeserializerTest extends AnyFlatSpec with Matchers {

  "JsonDeserializer" should "succeed in deserilizing simple json" in {
    val simpleJson: String ="""
      | {
      |   "changeTime": "2016-01-01T00:30:00.001059",
      |   "after": {
      |     "ambientTemp": 79,
      |     "schedule": false,
      |     "lastAlertTs": "2016-01-01T04:36:00.033185",
      |     "setpoint": {
      |       "heatTemp": 67,
      |       "coolTemp": 80
      |     }
      |   },
      |   "before": {
      |     "ambientTemp": 77,
      |     "schedule": true,
      |     "lastAlertTs": "2015-12-31T06:31:00.005702",
      |     "setpoint": {
      |       "heatTemp": 69,
      |       "coolTemp": 79
      |     }
      |   }
      | }
    """.stripMargin

    val expectedDelta = ThermostatDelta(changeTime = LocalDateTime.parse("2016-01-01T00:30:00.001059"),
      before = ThermostatData(
        lastAlertTs = LocalDateTime.parse("2015-12-31T06:31:00.005702"),
        ambientTemp = 77,
        schedule = true,
        setpoint = SetTemperature(heatTemp = 69, coolTemp = 79)),
      after = ThermostatData(
        lastAlertTs = LocalDateTime.parse("2016-01-01T04:36:00.033185"),
        ambientTemp = 79,
        schedule = false,
        setpoint = SetTemperature(heatTemp = 67, coolTemp = 80)
      ))

    val expectedOutput = Some(expectedDelta)

    JsonDeserializer.deserialize(simpleJson) should be (expectedOutput)
  }

  it should "fail to deserializing nonsense" in {
    val expectedOutput = None
    JsonDeserializer.deserialize("Not something it can handle") should be (expectedOutput)
  }

}
