# śiva format शिव for the JVM [![Build Status](https://travis-ci.org/src-d/siva-java.svg?branch=master)](https://travis-ci.org/src-d/siva-java)

This library is a Java implementation of [siva format](https://github.com/src-d/go-siva/blob/master/SPEC.md).
It  is intended to be used with any JVM language.
The main implementation is written in Go [here](https://github.com/src-d/go-siva).

This java library offers an API to read and unpack [siva files](https://github.com/src-d/go-siva/blob/master/SPEC.md) but not to write them yet.

## Usage

`siva-java` is available on [maven central](http://search.maven.org/#search%7Cga%7C1%7Csiva-java). To include it as a dependency in your project managed by [sbt](http://www.scala-sbt.org/) add the dependency to your `build.sbt` file:

```scala
libraryDependencies += "tech.sourced" % "siva-java" % "0.1.1"
```

On the other hand, if you use [maven](https://maven.apache.org/) to manage your dependencies, you must add the dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>tech.sourced</groupId>
    <artifactId>siva-java</artifactId>
    <version>0.1.1</version>
</dependency>
```

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
        try {
            LOGGER.log(Level.INFO, "unpacking siva-file");
            SivaReader sivaReader = new SivaReader(new File(DEFAULT_SIVA_FILE));
            List<IndexEntry> index = sivaReader.getIndex().getFilteredIndex().getEntries();
            for (IndexEntry indexEntry : index) {
                InputStream entry = sivaReader.getEntry(indexEntry);
                Path outPath = Paths.get(SIVA_UNPACKED_DIR.concat(indexEntry.getName()));
                FileUtils.copyInputStreamToFile(entry, new File(outPath.toString()));
            }

            sivaReader.close();
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

It leaves the jar file  in `./target/scala-2.11/siva-java_2.11-0.1.0-SNAPSHOT.jar`

### Tests

Just run:

    make test


### Clean

To clean the project:

    make clean


## License

See [LICENSE](LICENSE).
