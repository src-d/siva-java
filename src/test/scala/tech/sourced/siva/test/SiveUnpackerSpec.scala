package tech.sourced.siva.test

import java.io.File
import java.nio.file.Paths

import org.apache.commons.io.FileUtils
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}

import tech.sourced.siva.SivaUnpacker


class SiveUnpackerSpec extends FlatSpec with Matchers with BeforeAndAfterAll {
  val dstDir = "./basic-unpacked-test"

  "unpack" should "be able to create all files" in {
    val sivaUnpacker = new SivaUnpacker(SivaReaderSpec.getReader("basic.siva"), dstDir)

    sivaUnpacker.unpack()

    SivaReaderSpec.filenames.foreach { filename =>
      val file = Paths.get(dstDir, filename).toFile
      file.exists() should be(true)
    }
  }

  override def afterAll() = {
    FileUtils.deleteDirectory(new File(dstDir))
  }

}
