package com.energyHub.interview.thermostat

import java.io.{File, FileInputStream}
import java.util.Date

import scala.util.{Failure, Success, Try}

object LocalFileReader {

  def readDirectoryOfGzippedFiles(directoryName: String): Try[Seq[String]] = {
    try {
      val baseDirectory = new File(directoryName)
      if (baseDirectory.isDirectory) {
        Try({
          baseDirectory.listFiles()
            .sortBy(file => file.getName)
            .flatMap(flatMapRead)
        })
      } else {
        Failure(new RuntimeException("The provided string was not a directory"))
      }
    } catch {
      case exception: Exception => Failure(exception)
    }
  }

  def readGzippedFileByLine(fileName: String): Try[Seq[String]] = {
    Try({
      val fileStream = new FileInputStream(fileName)
      new Gunzipper(fileStream).toSeq
    })
  }

  private def flatMapRead(file: File): Seq[String] = {
    {
      if (file.isDirectory)
        readDirectoryOfGzippedFiles(file.getAbsolutePath)
      else
        readGzippedFileByLine(file.getAbsolutePath)
    } match {
      case Success(value) => value
      case Failure(exception) => throw exception
    }
  }
}


