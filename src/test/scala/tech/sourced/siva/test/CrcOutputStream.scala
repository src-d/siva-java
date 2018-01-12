package tech.sourced.siva.test

import java.io.OutputStream
import java.util.zip.CRC32

/**
  * OutputStream that will just generate the CRC32 signature of the content
  */
class CrcOutputStream extends OutputStream {
  private val crc32 = new CRC32()

  override def write(b: Int): Unit = crc32.update(b)

  override def write(b: Array[Byte]): Unit = crc32.update(b)

  override def write(b: Array[Byte], off: Int, len: Int): Unit =
    crc32.update(b, off, len)

  def getValue: Long = crc32.getValue

  def reset(): Unit = crc32.reset()
}
