package com.energyHub.interview

import java.io.File
import java.time.LocalDateTime
import java.util.function.Predicate

import com.energyHub.interview.thermostat.ThermostatData
import scopt.{OParser, OParserBuilder}

case class CommandConfig(filePath: String = null,
                         fieldAccessors: Vector[ThermostatData => _] = Vector(),
                         predicate: Predicate[ThermostatData] = _ => false)

/**
 * CommandArguments parses the commands that are given to the application in to useful inputs.
 * @param builder
 */
class CommandArguments(builder: OParserBuilder[CommandConfig]) {

  type FieldMapping[T] = (RegexWrapper[T], ThermostatData => T)
  private val fieldAccessorMapping: Map[String, FieldMapping[_]] = Map[String, FieldMapping[_]](
    "alert" -> (TimeRegexWrapper, (data: ThermostatData) => data.lastAlertTs.get),
    "temp" -> (NumberRegexWrapper, (data: ThermostatData) => data.ambientTemp.get),
    "scheduled" -> (BooleanRegexWrapper, (data: ThermostatData) => data.schedule.get),
    "heatTo" -> (NumberRegexWrapper, (data: ThermostatData) => data.setpoint.get.heatTemp.get),
    "coolTo" -> (NumberRegexWrapper, (data: ThermostatData) => data.setpoint.get.coolTemp.get),
    "eventTime" -> (TimeRegexWrapper, (data: ThermostatData) => data.eventTime.get),
  )

  val parser: OParser[Unit, CommandConfig] = {
    import builder._
    OParser.sequence(
      // Basic info
      programName("replay"),
      head("replay v0.1"),

      help('h', "help").text("prints this message"),

      // Details about picking files
      arg[String]("<filepath>...")
        .required()
        .validate(filepath => {
          try {
            new File(filepath)
          } catch {
            case e: Exception => failure(e.getMessage)
          }
          success
        })
        .action((input, config) => config.copy(filePath = input))
        .maxOccurs(1)
        .text("The file to be loaded into memory for processing.  Currently, only 1 file path can be provided.  " +
          "This can be a file or a directory."),

      /*
      // TODO Tie in the field requests to what actually outputs to end user
      opt[String]('f', "field")
        .validate(validateField)
        .action((input, config) => {
          config.copy(fieldAccessors = config.fieldAccessors :+ fieldAccessorMapping(input)._2)
        })
        .text("The fields from the data you can access.  By default, the entire event will be presented"),
       */

      // Information about the query options
      opt[Map[String, String]]('e', "eventCondition")
        .required()
        .maxOccurs(1)
        .valueName("field1=predicate1,field2=predicate2")
        .validate(k => {
          compressFailures {
            for (entry <- k) yield validateField(entry._1)
          }
        })
        .validate(k => {
          compressFailures {
            for (entry <- k) yield validatePredicate(entry._1, entry._2)
          }
        })
        .action(searchAction)
        //gt, lt, eq were used in place of <, >, = to avoid collisions with bash on the meaning of < and >.
        .text(
          "This is a map of search parameters.  Three types of predicates are support: Boolean, Number, and Time " +
            "predicates.  These predicates will be 'and'ed together for the search over provided files:" +
            "\n\t\t\t  Accepted Values: " +
            "\n\t\t\t\t  Boolean: eq-true, eq-false " +
            "\n\t\t\t\t  Number: eq-50, gt-50, lt-50 " +
            "\n\t\t\t\t  Time: lt-2020-10-01T12:25:00, gt-2020-10-01T12:25:00.  Time must be in ISO 8601 format to a 1 second resolution e.g. temp=gt-55,schedule=eq-false,eventTime=lt-2020-12-05T12:25:50"
            ),
      note("\n\tThis is the list of fields used in both `eventCondition` command parameters:\n %s"
        .format(fieldAccessorMapping.keySet.mkString("\n\t\t")))

    )
  }

  private def validateField(input: String): Either[String, Unit] = {
    if (!fieldAccessorMapping.contains(input)) Left("No field matching %s".format(input))
    else Right()
  }

  private def validatePredicate(key: String, value: String): Either[String, Unit] = {
    fieldAccessorMapping(key)._1.regex.findFirstMatchIn(value) match {
      case Some(_) => Right()
      case None => Left("The provide string doesn't match the possible predicates: %s".format(value))
    }
  }

  private def compressFailures(input: Iterable[Either[String, Unit]]): Either[String, Unit] = {
    input.reduce((v1, v2) => {
      if (v1.isLeft && v2.isLeft) Left(v1.left.get + "\n" + v2.left.get)
      else if (v1.isLeft) v1
      else if (v2.isLeft) v2
      else v1
    })
  }

  /**
   * This takes the quarries provided through the CLI and compiles them into a single predicate that can be applied
   *    to an arbitrary ThermostatData object.
   */
  private def searchAction(input: Map[String, String], config: CommandConfig): CommandConfig = {
    def buildDataPredicate[T](mapping: FieldMapping[T], inputString: String): Predicate[ThermostatData] = {
      (data: ThermostatData) => {
        val value: T = mapping._2.apply(data)
        val tester: Predicate[T] = mapping._1.regexToPredicate(inputString)
        tester.test(value)
      }
    }

    // Create a collection of predicates against ThermostatData
    val predicateChain: Iterable[Predicate[ThermostatData]] = for (entry <- input)
      yield {
        val booleanString = entry._2
        // This match statement exists solely to take the generics wildcards [_] and make them concrete [Int].
        fieldAccessorMapping(entry._1) match {
          case mapping: FieldMapping[Double] => buildDataPredicate[Double](mapping, booleanString)
          case mapping: FieldMapping[Boolean] => buildDataPredicate[Boolean](mapping, booleanString)
          case mapping: FieldMapping[LocalDateTime] => buildDataPredicate[LocalDateTime](mapping, booleanString)
        }
      }

    // 'and's together the individual predicates into a single predicate.
    val outputPredicate: Predicate[ThermostatData] = (data: ThermostatData) => {
      predicateChain
        .map(_.test(data))
        .reduce((v1, v2) => if (v1 && v2) true else false)
    }

    config.copy(predicate = outputPredicate)
  }

}
