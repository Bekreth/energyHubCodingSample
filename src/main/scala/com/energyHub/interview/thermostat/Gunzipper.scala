package com.energyHub.interview.thermostat

import java.io.{BufferedReader, InputStream, InputStreamReader}
import java.util.zip.GZIPInputStream

class Gunzipper(inputStream: InputStream) extends Iterator[String] {

  private val gunzipper = new GZIPInputStream(inputStream)
  private val inputStreamReader = new InputStreamReader(gunzipper)
  private val bufferedReader = new BufferedReader(inputStreamReader)

  override def hasNext: Boolean = bufferedReader.ready()
  override def next(): String = bufferedReader.readLine()
}
