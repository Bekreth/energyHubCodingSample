package com.energyHub.interview.search

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
    //TODO: This currently takes in an Iterator given that I need to make a check on whether I've processed all events.
    //    In the fiction where this is cloud deployed, this should be migrated to a lazy streaming approach instead
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
