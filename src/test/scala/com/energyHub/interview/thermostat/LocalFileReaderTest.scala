package com.energyHub.interview.thermostat

import org.junit.runner.RunWith
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.junit.JUnitRunner

import scala.util.{Failure, Success}

@RunWith(classOf[JUnitRunner])
class LocalFileReaderTest extends AnyFlatSpec with Matchers{

  val testRootDirectory = "src/test/resources/localFileReaderTest"

  /**
   * ## NOTE ##
   * This test requires that the nested files are located in the src/test/resources.  There is a bash
   *    script there that properly prepares the test when gradle is run.  If the first test fails, check
   *    this location first.
   */
  "LocalFileReader readDirectoryOfGzippedFiles" should "work" in {
    val testDirectory = "%s/nested/2019".format(testRootDirectory)

    val expectedSeq = for(
      month <- 1 to 12;
      day <- 1 to 30;
      count <- 1 to 10;
      lineNumber <- 1 to 10
    ) yield "2019 %02d %02d %02d Line:%d"
      .format(month, day, count, lineNumber)

    val expectedOutput = expectedSeq.toArray

    LocalFileReader.readDirectoryOfGzippedFiles(testDirectory) match {
      case Failure(exception) => fail(exception)
      case Success(value) => {
        val actualOutput = value.toArray
        actualOutput should be (expectedOutput)
      }
    }
  }

  it should "fail for nonexisistant directories" in {
    LocalFileReader.readDirectoryOfGzippedFiles("NotADirectory") match {
      case Success(_) => fail("Expected a failure")
      case Failure(exception) => succeed
    }
  }

  it should "fail if a nested file can't be parsed" in {
    val testDirectory = "%s/nested".format(testRootDirectory)
    LocalFileReader.readDirectoryOfGzippedFiles(testDirectory) match {
      case Success(_) => fail("Expected a failure")
      case Failure(exception) => succeed
    }
  }
}
