package com.energyHub.interview

import java.time.LocalDateTime

import com.typesafe.scalalogging.LazyLogging
import scopt.{OParser, OptionParser}

case class CommandConfig(targetDate: Option[LocalDateTime] = None)

object Main extends App 
  with LazyLogging {

//  val builder = OParser.builder[CommandConfig]

  /*
  val parser1 = {
    import builder._
    OParser.sequence(
      programName("something"),
      head(""),
      opt[String]('t', "timeStamp")
        .action((inString, config) => config.copy(targetDate = Some(LocalDateTime.parse(inString))))
        .text("The desired time")
    )
  }

  OParser.parse(parser1, args, CommandConfig()) match {
    case Some(input) =>
      println(input)
    case None =>
      println("Something went wrong")
  }
   */

}
