package com.energyHub.interview

import com.energyHub.interview.search.StreamSearcher.{CurrentSearchState, SurroundingEvents}
import com.energyHub.interview.search.{ResultsView, StreamSearcher}
import com.energyHub.interview.thermostat.LocalFileReader
import com.energyHub.interview.thermostat.deserializer.JsonDeserializer
import scopt.OParser

import scala.util.{Failure, Success}

object Main extends App {

  val builder = OParser.builder[CommandConfig]
  OParser.parse(new CommandArguments(builder).parser, args, CommandConfig()) match {
    case Some(config) => runSearch(config)
    case None => println("Invalid input")
  }

  def runSearch(config: CommandConfig): Unit = {
    LocalFileReader.readLocalFile(config.filePath) match {
      case Failure(exception) => println("Failed to process request: %s", exception)
      case Success(lines) => {
        val eitherOutput = StreamSearcher
          .searchStream(input = lines
            .map(JsonDeserializer.deserialize)
            .filter(_.nonEmpty)
            .map(_.get),
          predicate = config.predicate)

        outputResults(config, eitherOutput)
      }
    }
  }

  def outputResults(config: CommandConfig, eitherOutput: Either[SurroundingEvents, CurrentSearchState]): Unit = {
    eitherOutput match {
      case Left(value) => {
        val before = ResultsView.readThermostatData(value._1)
        val after = ResultsView.readThermostatData(value._2)
        println("events were found.\n\tbefore:  %s\n\tafter:  %s"
          .format(ResultsView.serialize(before), ResultsView.serialize(after)))
      }
      case Right(value) => {
        //TODO: Was aiming to save this in working memory and provide end user with an interactive shell to load
        //    more data dynamically and continue the search.
        println(("Unable to find an event matching your search.  " +
          "Final state during out put is: %s").format(value))
      }
    }
  }

}
