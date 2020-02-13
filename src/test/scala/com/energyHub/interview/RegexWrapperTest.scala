package com.energyHub.interview

import java.time.LocalDateTime

import org.junit.runner.RunWith
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class RegexWrapperTest extends AnyFlatSpec with Matchers {

  "BooleanRegexWrapper" should "create a valid BooleanPredicate" in {
    val truePredicate = BooleanRegexWrapper.regexToPredicate("_=true")
    val falsePredicate = BooleanRegexWrapper.regexToPredicate("_=false")

    truePredicate.test(true) should be (true)
    falsePredicate.test(false) should be (true)
  }

  "NumberRegexWrapper" should "create a valid NumberPredicate" in {
    val lessPredicate = NumberRegexWrapper.regexToPredicate("_<10")
    val equalPredicate = NumberRegexWrapper.regexToPredicate("_=10")
    val greaterPredicate = NumberRegexWrapper.regexToPredicate("_>10")

    lessPredicate.test(5) should be (true)
    equalPredicate.test(10) should be (true)
    greaterPredicate.test(15) should be (true)
  }

  "TimeRegexWrapper" should "create a valid TimePredicate" in {
    val baseTimeString = "2020-02-02T12:00:00"

    val beforePredicate = TimeRegexWrapper.regexToPredicate("_<%s".format(baseTimeString))
    val afterPredicate = TimeRegexWrapper.regexToPredicate("_>%s".format(baseTimeString))

    beforePredicate.test(LocalDateTime.parse("2019-02-02T12:00:00")) should be (true)
    afterPredicate.test(LocalDateTime.parse("2021-02-02T12:00:00")) should be (true)
  }

}
