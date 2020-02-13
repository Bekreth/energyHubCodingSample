package com.energyHub.interview

import java.time.LocalDateTime
import java.util.function.Predicate

import com.energyHub.interview.search.StreamSearcher
import com.energyHub.interview.search.StreamSearcher.{CurrentSearchState, SurroundingEvents}
import com.energyHub.interview.thermostat.{SetTemperature, ThermostatData, ThermostatDelta}
import org.junit.runner.RunWith
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class StreamSearcherTest extends AnyFlatSpec with Matchers {

  val baseTime: LocalDateTime = LocalDateTime.parse("2020-02-14T15:20:30.000555")

  def baseThermometerData(time: LocalDateTime) = ThermostatData(
    lastAlertTs = Some(baseTime.minusHours(3)),
    ambientTemp = Some(10),
    schedule = Some(false),
    setpoint = Some(SetTemperature(heatTemp = Some(10), coolTemp = Some(5))),
    eventTime = Some(time)
  )

  "StreamSearcher" should "return left when successfully finding an event" in {

    val input = {for (i <- -10 to 10) yield baseTime.plusMinutes(i)}
      .map(time => ThermostatDelta(changeTime = time,
        before = baseThermometerData(time),
        after = baseThermometerData(time)))


    val simpleTimePredicate: Predicate[ThermostatData] = data => {
      data.eventTime.get.isAfter(baseTime.plusMinutes(5).minusSeconds(30))
    }

    val expectedOutput: SurroundingEvents = (baseThermometerData(baseTime.plusMinutes(4)), baseThermometerData(baseTime.plusMinutes(5)))
    StreamSearcher.searchStream(input = input.toIterator, predicate = simpleTimePredicate) match {
      case Left(value) => {
        value should be (expectedOutput)
      }
      case Right(_) => fail("Expected there to be a surrounding event")
    }
  }

  it should "provide a failed output if the data state hasn't properly warmed" in {
    val input = {for (i <- -10 to 10) yield baseTime.plusMinutes(i)}
      .map(time => ThermostatDelta(changeTime = time,
        before = baseThermometerData(time).copy(lastAlertTs = None),
        after = baseThermometerData(time).copy(lastAlertTs = None)))


    val simpleTimePredicate: Predicate[ThermostatData] = data => {
      data.eventTime.get.isAfter(baseTime.plusMinutes(5).minusSeconds(30))
    }

    val expectedOutput: CurrentSearchState = baseThermometerData(baseTime.plusMinutes(10)).copy(lastAlertTs = None)
    StreamSearcher.searchStream(input = input.toIterator, predicate = simpleTimePredicate) match {
      case Left(_) => fail("Expected there a single output event")
      case Right(value) => value should be (expectedOutput)
    }
  }

}
