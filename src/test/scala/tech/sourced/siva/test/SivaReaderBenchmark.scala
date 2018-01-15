package tech.sourced.siva.test

import org.scalameter.api._
import org.scalameter.picklers.noPickler._

import scala.collection.JavaConverters._

object SivaReaderBenchmark extends Bench.ForkedTime {
  val files: Gen[String] = Gen.enumeration("files")(
    "basic.siva",
    "deleted.siva",
    "dirs.siva",
    "overwritten.siva"
  )

  performance of "Read" in {
    measure method "CompleteIndex" in {
      using(files) in {
        f => {
          val reader = Utils.getReader(f)
          val entries = reader.getIndex.getCompleteIndex.getEntries.asScala

          for {
            entry <- entries
          } yield {
            entry.getName
          }
        }
      }
    }

    measure method "FilteredIndex" in {
      using(files) in {
        f => {
          val reader = Utils.getReader(f)
          val entries = reader.getIndex.getFilteredIndex.getEntries.asScala

          for {
            entry <- entries
          } yield {
            entry.getName
          }
        }
      }
    }

    measure method "Entries" in {
      using(files) in {
        f => {
          val reader = Utils.getReader(f)
          val entries = reader.getIndex.getFilteredIndex.getEntries

          Utils.checkEntries(entries, reader)
        }
      }
    }
  }
}
