package tech.sourced.siva.test

import java.io.File
import java.util.zip.CRC32

import org.apache.commons.io.IOUtils
import org.scalatest.{FlatSpec, Matchers}
import tech.sourced.siva.{IndexEntry, SivaReader}

import scala.collection.JavaConverters._

object Utils extends FlatSpec with Matchers {
  def getReader(filename: String): SivaReader = {
    val resourceUrl = getClass.getResource("/" + filename)
    val file = new File(resourceUrl.toURI)

    file.exists() should be(true)

    new SivaReader(file)
  }

  def checkEntries(entries: java.util.List[IndexEntry], reader: SivaReader): Unit = {
    val crc32 = new CRC32()

    entries.asScala.foreach(e => {
      crc32.reset()
      val is = reader.getEntry(e)
      val content = IOUtils.toByteArray(is)

      crc32.update(content)

      e.getCrc32 should be(crc32.getValue)
    })
  }
}
