package com.energyHub.interview.thermostat

import java.io.FileInputStream

import org.junit.runner.RunWith
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class GunzipperTest extends AnyFlatSpec with Matchers {

  val testingFile = "src/test/resources/gunzipperTest.txt.gz"

  "Gunzipper" should "successfully decompress a provided file stream" in {
    val inputStream = new FileInputStream(testingFile)
    val gunzipper = new Gunzipper(inputStream)

    for (i <- 1 to 25) {
      gunzipper.hasNext should be (true)
      gunzipper.next() should be ("Line %d".format(i))
    }
    gunzipper.hasNext should be (false)
  }
}
