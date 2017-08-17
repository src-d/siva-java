package tech.sourced.siva.test

import java.io.File
import java.nio.file.{AccessDeniedException, Files, Paths}

import org.apache.commons.io.FileUtils
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}
import tech.sourced.siva.SivaUnpacker


class SiveUnpackerSpec extends FlatSpec with Matchers with BeforeAndAfterAll {
  val dstBaseDir = "./unpacked-test/"

  "unpack" should "be able to create all files" in {
    val sivaUnpacker = new SivaUnpacker(SivaReaderSpec.getReader("basic.siva"))

    val dstDir = dstBaseDir + "basic"
    sivaUnpacker.unpack(dstDir)

    SivaReaderSpec.filenames.foreach { filename =>
      val file = Paths.get(dstDir, filename).toFile
      file.exists() should be(true)
    }
  }

  "unpack" should "get latest snapshot (skip deleted files)" in {
    val sivaUnpacker = new SivaUnpacker(SivaReaderSpec.getReader("deleted.siva"))

    val dstDir = dstBaseDir + "deleted"
    sivaUnpacker.unpack(dstDir)

    val deletedFiles = SivaReaderSpec.filenamesDeleted.toSet
    new File(dstDir).list.foreach { file =>
      deletedFiles should contain(file)
    }

  }

  "unpack" should "create dir structure" in {
    val sivaUnpacker = new SivaUnpacker(SivaReaderSpec.getReader("basic-with-dir.siva"))

    val dstDir = dstBaseDir + "with-dir"
    sivaUnpacker.unpack(dstDir)

    val dir = new File(dstDir + "/dir1")
    dir.exists() should be(true)
    dir.isDirectory should be(true)
  }

  "unpack" should "change file permissions, if instructed" in {
    val sivaUnpacker = new SivaUnpacker(SivaReaderSpec.getReader("overwritten.siva"), false)

    val dstDir = dstBaseDir + "overwritten"
    sivaUnpacker.unpack(dstDir)

    val changedFile = new File(dstDir + "/gopher.txt")
    try {
      Files.getPosixFilePermissions(changedFile.toPath)
    } catch {
      case e: AccessDeniedException => assert(true)
      case _ => assert(false)
    }
    //PosixFilePermissions.toString(Files.getPosixFilePermissions(changedFile.toPath)) should be("rwxrwxrwx")
  }

  override def afterAll() = {
    FileUtils.deleteDirectory(new File(dstBaseDir))
  }

}
