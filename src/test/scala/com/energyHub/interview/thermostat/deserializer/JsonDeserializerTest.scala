package com.energyHub.interview.thermostat.deserializer

import java.time.LocalDateTime

import com.energyHub.interview.thermostat.{SetTemperature, ThermostatData, ThermostatDelta}
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
        lastAlertTs = Some(LocalDateTime.parse("2015-12-31T06:31:00.005702")),
        ambientTemp = Some(77),
        schedule = Some(true),
        setpoint = Some(SetTemperature(heatTemp = Some(69), coolTemp = Some(79)))),
      after = ThermostatData(
        lastAlertTs = Some(LocalDateTime.parse("2016-01-01T04:36:00.033185")),
        ambientTemp = Some(79),
        schedule = Some(false),
        setpoint = Some(SetTemperature(heatTemp = Some(67), coolTemp = Some(80)))
      ))

    val expectedOutput = Some(expectedDelta)

    JsonDeserializer.deserialize(simpleJson) should be (expectedOutput)
  }

  it should "fail to deserializing nonsense" in {
    val expectedOutput = None
    JsonDeserializer.deserialize("Not something it can handle") should be (expectedOutput)
  }

}
