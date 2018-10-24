# śiva format शिव for the JVM [![Build Status](https://travis-ci.org/src-d/siva-java.svg?branch=master)](https://travis-ci.org/src-d/siva-java)

This library is a Java implementation of [siva format](https://github.com/src-d/go-siva/blob/master/SPEC.md).
It  is intended to be used with any JVM language.
The main implementation is written in Go [here](https://github.com/src-d/go-siva).

This java library offers an API to read and unpack [siva files](https://github.com/src-d/go-siva/blob/master/SPEC.md) but not to write them yet.

## Usage

`siva-java` is available on [maven central](http://search.maven.org/#search%7Cga%7C1%7Csiva-java). To include it as a dependency in your project managed by [sbt](http://www.scala-sbt.org/) add the dependency to your `build.sbt` file:

```scala
libraryDependencies += "tech.sourced" % "siva-java" % "[version]"
```

On the other hand, if you use [maven](https://maven.apache.org/) to manage your dependencies, you must add the dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>tech.sourced</groupId>
    <artifactId>siva-java</artifactId>
    <version>[version]</version>
</dependency>
```

In both cases, replace `[version]` with the [latest siva-java version](http://search.maven.org/#search%7Cga%7C1%7Csiva-java).

## Example of Usage

```java
package com.github.mcarmonaa.sivaexample;

import org.apache.commons.io.FileUtils;
import tech.sourced.siva.IndexEntry;
import tech.sourced.siva.SivaReader;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static final String SIVA_DIR = "/tmp/siva-files/";
    private static final String SIVA_UNPACKED_DIR = "/tmp/siva-unpacked/";
    private static final String DEFAULT_SIVA_FILE = SIVA_DIR + "/aac052c42c501abf6aa8c3509424e837bb27e188.siva";
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        LOGGER.log(Level.INFO, "unpacking siva-file");
        try (SivaReader sivaReader = new SivaReader(new File(DEFAULT_SIVA_FILE))) {
            List<IndexEntry> index = sivaReader.getIndex().getFilteredIndex().getEntries();
            for (IndexEntry indexEntry : index) {
                InputStream entry = sivaReader.getEntry(indexEntry);
                Path outPath = Paths.get(SIVA_UNPACKED_DIR.concat(indexEntry.getName()));
                FileUtils.copyInputStreamToFile(entry, new File(outPath.toString()));
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, ex.toString(), ex);
        }
    }
}
```

## Development

### Build

To build the project and generate a jar file:

    make build

It leaves the jar file  at `./target/siva-java-[version].jar`, being `[version]` the version specified in the `build.sbt`

### Tests

Just run:

    make test


### Clean

To clean the project:

    make clean

## Limitations

Some known limitations and implementation divergences regarding the [main siva reference specification](https://github.com/src-d/go-siva/blob/master/SPEC.md)

All the issues commented below are related to the `index` part of the blocks since that is where *siva* really places the metadata. Most of the meta-information is encoded as unsigned values, because of this, most of the problems come from the lack of unsigned values in the `JVM`.

To avoid these limitations, in some cases, a cast to a bigger number type and a binary `AND` operation with a mask solves the problem. The trick consists of:

```
unsigned int8 (byte in Go): 255

if you read this byte in Java, it interprets the value as signed. So the same bits in Java result on:

signed int8 (byte in Java): -1

Casting this value to a java integer, keeps the value as  -1, so we apply a binary mask, with the less weight byte set to all "ones" and the rest of the byte to "zeros":

byte b = readByte() // 255 read, but in java the value is -1
int mask = 0x000000FF
int n = b & mask // now n is an integer storing the value 255

```

This procedure is related on how `JVM` encodes the number values using [two's complement](https://en.wikipedia.org/wiki/Two%27s_complement) and it can apply for all the types which can be cast to a bigger number type.

***Unsigned Integer 64 Limitation!***: a siva file with a value in those fields that the specification encodes as `uint64 ` can contain values in range [0, 2<sup>64</sup>-1] while java implementation only supports values in range [0, 2<sup>64-1</sup>-1]. There's no a number type bigger than a `long` (int64) in java, so this can't be avoided.

Next, are pointed those parts of the `index` affected by different issues:

- Index Signature: [The reference specification](https://github.com/src-d/go-siva/blob/master/SPEC.md) says that a sequence of three bytes (`IBA`) is used as the signature but for the [reference implementation in Go](https://github.com/src-d/go-siva) a byte is an `uint8` while in java a byte is an `int8`. The current java implementation doesn't take care about this since the three bytes used are all of them values less than 127, so these values are read properly.

- Version: [The reference specification](https://github.com/src-d/go-siva/blob/master/SPEC.md) tells about use an `uint8` for this. For the moment it's read with java byte because siva is at version `1`.  A version greater than 127 would brake this implementation .

- Index Entry:
    - UNIX mode: is encoded as `uint32`, so in java implementation is cast to a long.
    - The offset of the file content, relative to the beginning of the block: this is an `uint64` value, so the implementation just read it as a long and check that is not negative. ***Unsigned Integer 64 Limitation!***
    - Size of the file content: encoded as a `uint64`, check no negative. ***Unsigned Integer 64 Limitation!***
    - CRC32: `uint32` value cast to a `long` java type.
    - Flags: `uint32` value, it's read without cast type since it only can contain values `0 (No Flags)` or `1 (Deleted)`.

- Index Footer:
    - Number of entries in the block:  `uint32` value cast to `long` java type.
    - Index Size in bytes: `uint64` value can't be cast, check no negative. ***Unsigned Integer 64 Limitation!***
    - Block size in bytes: `uint64`value cant't be cast, check no negative. ***Unsigned Integer 64 Limitation!***
    - CRC32: `uint32` value cast to a `long` java type.

***Other comments***: This java implementation verify the integrity of the index with the `CRC` in the Index Footer. The integrity of the files should be checked optionally with the `CRC` kept in the Index Entry by the clients of this library.

## License

See [LICENSE](LICENSE).
