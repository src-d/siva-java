package tech.sourced.siva.test

import java.io.File
import java.nio.file.attribute.{FileTime, PosixFilePermissions}
import java.util.concurrent.TimeUnit
import java.util.zip.CRC32

import org.apache.commons.io.IOUtils
import org.scalatest._
import org.scalatest.prop.TableDrivenPropertyChecks._
import tech.sourced.siva.SivaReader

import scala.collection.JavaConverters._

class SivaReaderSpec extends FlatSpec with Matchers {
  private val filenames = "gopher.txt" :: "readme.txt" :: "todo.txt" :: Nil
  private val filenamesDeleted = "readme.txt" :: "todo.txt" :: Nil
  private val filenamesFolders =
    "numbers/1" ::
      "numbers/2" ::
      "numbers/3" ::
      "letters/a" ::
      "letters/b" ::
      "letters/c" :: Nil

  private val fixtures = Table(
    ("filename", "elements", "repeatedElements"),
    ("basic.siva", filenames, false),
    ("deleted.siva", filenamesDeleted, true),
    ("dirs.siva", filenamesFolders, false),
    ("overwritten.siva", filenames, true)
  )

  "Index" should "be read correctly" in {
    forAll(fixtures) { (filename: String, elements: List[String], repeated: Boolean) =>
      val sivaReader = getReader(filename)
      val completeIndex = sivaReader.getIndex.getCompleteIndex
      val filteredIndex = sivaReader.getIndex.getFilteredIndex

      filteredIndex.getEntries.asScala.map(_.getName) should contain allElementsOf elements

      if (repeated) {
        completeIndex.getEntries.size() shouldNot be(filteredIndex.getEntries.size())
      } else {
        completeIndex.getEntries.size() should be(filteredIndex.getEntries.size())
      }

      sivaReader.close()
    }
  }

  "Glob" should "obtain filtered elements only" in {
    forAll(fixtures) { (filename: String, _: List[String], _: Boolean) =>
      val sivaReader = getReader(filename)
      val completeIndex = sivaReader.getIndex.getCompleteIndex

      val names = completeIndex.glob("*.txt").asScala.map(_.getName)

      names.foreach(n => n should endWith(".txt"))

      sivaReader.close()
    }
  }

  "getEntry" should "read correctly a file into a siva file" in {
    forAll(fixtures) { (filename: String, elements: List[String], repeated: Boolean) =>
      val sivaReader = getReader(filename)

      val entries = sivaReader.getIndex.getCompleteIndex.getEntries.asScala

      val crc32 = new CRC32()

      entries.foreach(e => {
        crc32.reset()

        val is = sivaReader.getEntry(e)
        val content = IOUtils.toByteArray(is)

        crc32.update(content)

        e.getCrc32 should be(crc32.getValue)
      })

      sivaReader.close()
    }
  }

  "file mode" should "change if the file has been overwritten" in {
    val sivaReader = getReader("overwritten.siva")

    val entry = sivaReader.getIndex.getFilteredIndex.glob("gopher.txt").asScala.head
    PosixFilePermissions.toString(entry.getFileMode) should be("rwxrwxrwx")
    entry.getModificationTime.to(TimeUnit.NANOSECONDS) should be(1502116728928289494L)

    sivaReader.getIndex.getCompleteIndex.glob("gopher.txt").size() should be(2)

    sivaReader.close()
  }

  private def getReader(filename: String): SivaReader = {
    val resourceUrl = getClass.getResource("/" + filename)
    val file = new File(resourceUrl.toURI)

    file.exists() should be(true)

    new SivaReader(file)
  }
}
