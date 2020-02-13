package com.energyHub.interview

import java.util.function.Predicate

import com.energyHub.interview.thermostat.{ThermostatData, ThermostatDelta}

object StreamSearcher {
  type SurroundingEvents = (ThermostatData, ThermostatData)
  type CurrentSearchState = ThermostatData

  def searchStream(startingState: ThermostatData = ThermostatData(),
                   input: Iterator[ThermostatDelta],
                   predicate: Predicate[ThermostatData]): Either[SurroundingEvents, CurrentSearchState] = {
    var priorState = startingState.copy()
    var nextState = startingState.copy()

    def targetData(): Boolean = priorState.warmed() && predicate.test(nextState)

    do {
      priorState = nextState.copy()
      val delta = input.next()
      nextState = priorState.copyToNewState(delta.after).copy(eventTime = Some(delta.changeTime))
    }
    while (input.hasNext && !targetData())

    if (targetData()) Left(priorState, nextState)
    else Right(nextState)
  }

}
