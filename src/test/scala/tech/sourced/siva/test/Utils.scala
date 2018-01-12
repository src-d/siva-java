package tech.sourced.siva.test

import java.io.File

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
    val os = new CrcOutputStream()
    entries.asScala.foreach(e => {
      os.reset()
      val is = reader.getEntry(e)

      IOUtils.copy(is, os)

      e.getCrc32 should be(os.getValue)
    })
  }
}
