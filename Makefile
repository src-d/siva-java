# Scala version
SCALA_VERSION ?= 2.11.11

# if TRAVIS_SCALA_VERSION defined SCALA_VERSION is overrided
ifneq ($(TRAVIS_SCALA_VERSION), )
        SCALA_VERSION := $(TRAVIS_SCALA_VERSION)
endif

SBT = ./sbt ++$(SCALA_VERSION)


# Rules
all: clean build

.PHONY: fatjar test clean
build:
	$(SBT) package

fatjar:
	$(SBT) assembly

test:
	$(SBT) test

travis-test:
	$(SBT) clean coverage test coverageReport scalastyle test:scalastyle checkstyle test:checkstyle

clean:
	$(SBT) clean


