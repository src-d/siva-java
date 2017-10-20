# Scala version
SCALA_VERSION ?= 2.11.11

# if TRAVIS_SCALA_VERSION defined SCALA_VERSION is overrided
ifneq ($(TRAVIS_SCALA_VERSION), )
        SCALA_VERSION := $(TRAVIS_SCALA_VERSION)
endif

SBT = ./sbt ++$(SCALA_VERSION)


# Rules
all: clean build

.PHONY: build test clean travis-test
build:
	$(SBT) package

test:
	$(SBT) test

travis-test:
	$(SBT) clean coverage test coverageReport scalastyle test:scalastyle checkstyle test:checkstyle

clean:
	$(SBT) clean

maven-release:
	$(SBT) publishLocal && \
	$(SBT) publishSigned && \
	$(SBT) sonatypeRelease