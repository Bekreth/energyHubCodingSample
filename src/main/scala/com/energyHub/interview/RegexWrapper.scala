package com.energyHub.interview

import java.time.LocalDateTime
import java.util.function.Predicate

import scala.util.matching.Regex

sealed trait RegexWrapper[T] {
  val regex: Regex
  final def regexToPredicate(input: String): Predicate[T] = regex.findFirstMatchIn(input).map(handleMatches).get

  protected def handleMatches(matches: Regex.Match): Predicate[T]
}

object BooleanRegexWrapper extends RegexWrapper[Boolean] {
  override val regex: Regex = new Regex("eq-(true|false)")
  override protected def handleMatches(matches: Regex.Match): Predicate[Boolean] = {
    matches.group(1) match {
      case "true" => (x: Boolean) => x
      case "false" => (x: Boolean) => !x
    }
  }
}

object NumberRegexWrapper extends RegexWrapper[Double] {
  override val regex: Regex = new Regex("(lt|gt|sq)-(\\d+)")
  override protected def handleMatches(matches: Regex.Match): Predicate[Double] = {
    val target = matches.group(2).toInt
    matches.group(1) match {
      case "lt" => (x: Double) => x < target
      case "gt" => (x: Double) => x > target
      case "eq" => (x: Double) => x == target
    }
  }
}

object TimeRegexWrapper extends RegexWrapper[LocalDateTime] {
  override val regex: Regex = new Regex("(lt|gt)-(\\d{4}-\\d{1,2}-\\d{1,2}T\\d{1,2}:\\d{2}:\\d{2})")
  override protected def handleMatches(matches: Regex.Match): Predicate[LocalDateTime] = {
    val target = LocalDateTime.parse(matches.group(2))
    matches.group(1) match {
      case "lt" => (x: LocalDateTime) => x.isBefore(target)
      case "gt" => (x: LocalDateTime) => x.isAfter(target)
    }
  }
}


