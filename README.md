# Tube

[![Java 8+](https://img.shields.io/badge/Java-8+-4c7e9f.svg)](http://java.oracle.com)
[![Build Status](https://travis-ci.org/uchuhimo/tube.svg?branch=master)](https://travis-ci.org/uchuhimo/tube)

A prototype for dataflow graph API in Java.

## Prerequisites

- JDK 1.8 or higher

## Build from source

Build library with Gradle using the following command:

```
gradlew clean assemble
```

Test library with Gradle using the following command:

```
gradlew clean test
```

Since Gradle has excellent incremental build support, you can usually omit executing the `clean` task.

Install library in a local Maven repository for consumption in other projects via the following command:

```
gradlew clean install
```

# License

© uchuhimo, 2017. Licensed under an [Apache 2.0](./LICENSE) license.
