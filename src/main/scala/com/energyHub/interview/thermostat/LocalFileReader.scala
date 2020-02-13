package com.energyHub.interview.thermostat

import java.io.{File, FileInputStream}

import scala.util.{Failure, Success, Try}

object LocalFileReader {

  def readLocalFile(fileName: String): Try[Iterator[String]] = {
    try {
      val file = new File(fileName)
      if (file.isDirectory) readDirectoryOfGzippedFiles(fileName)
      else readGzippedFileByLine(fileName)
    } catch {
      case exception: Exception => Failure(exception)
    }
  }

  def readDirectoryOfGzippedFiles(directoryName: String): Try[Iterator[String]] = {
    try {
      val baseDirectory = new File(directoryName)
      if (baseDirectory.isDirectory) {
        Try({
          baseDirectory.listFiles()
            .sortBy(file => file.getName)
            .flatMap(flatMapRead)
            .toIterator
        })
      } else {
        Failure(new RuntimeException("The provided string was not a directory"))
      }
    } catch {
      case exception: Exception => Failure(exception)
    }
  }

  def readGzippedFileByLine(fileName: String): Try[Iterator[String]] = {
    Try({
      val fileStream = new FileInputStream(fileName)
      new Gunzipper(fileStream)
    })
  }

  private def flatMapRead(file: File): Iterator[String] = {
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


