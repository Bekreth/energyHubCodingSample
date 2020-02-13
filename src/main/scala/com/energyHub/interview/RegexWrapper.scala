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
  override val regex: Regex = new Regex("_=(true|false)")
  override protected def handleMatches(matches: Regex.Match): Predicate[Boolean] = {
    matches.group(1) match {
      case "true" => (x: Boolean) => x
      case "false" => (x: Boolean) => !x
    }
  }
}

object NumberRegexWrapper extends RegexWrapper[Int] {
  override val regex: Regex = new Regex("_(<|>|=)(\\d+)")
  override protected def handleMatches(matches: Regex.Match): Predicate[Int] = {
    val target = matches.group(2).toInt
    matches.group(1) match {
      case "<" => (x: Int) => x < target
      case ">" => (x: Int) => x > target
      case "=" => (x: Int) => x == target
    }
  }
}

object TimeRegexWrapper extends RegexWrapper[LocalDateTime] {
  override val regex: Regex = new Regex("_(<|>)(\\d{4}-\\d{1,2}-\\d{1,2}T\\d{1,2}:\\d{2}:\\d{2})")
  override protected def handleMatches(matches: Regex.Match): Predicate[LocalDateTime] = {
    val target = LocalDateTime.parse(matches.group(2))
    matches.group(1) match {
      case "<" => (x: LocalDateTime) => x.isBefore(target)
      case ">" => (x: LocalDateTime) => x.isAfter(target)
    }
  }
}


