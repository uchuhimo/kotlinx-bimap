# kotlinx-bimap

[![Java 6+](https://img.shields.io/badge/Java-6+-4c7e9f.svg)](http://java.oracle.com)
[![Bintray](https://api.bintray.com/packages/uchuhimo/maven/kotlinx-bimap/images/download.svg)](https://bintray.com/uchuhimo/maven/kotlinx-bimap/_latestVersion)
[![JitPack](https://jitpack.io/v/uchuhimo/kotlinx-bimap.svg)](https://jitpack.io/#uchuhimo/kotlinx-bimap)
[![Build Status](https://travis-ci.org/uchuhimo/kotlinx-bimap.svg?branch=master)](https://travis-ci.org/uchuhimo/kotlinx-bimap)
[![codecov](https://codecov.io/gh/uchuhimo/kotlinx-bimap/branch/master/graph/badge.svg)](https://codecov.io/gh/uchuhimo/kotlinx-bimap)

A BiMap (bidirectional map) implementation for Kotlin.

## Using in your projects

This library are published to  [JCenter](https://bintray.com/uchuhimo/maven/kotlinx-bimap) and [JitPack](https://jitpack.io/#uchuhimo/kotlinx-bimap).

### Maven

Add Bintray JCenter repository to `<repositories>` section:

```xml
<repository>
    <id>central</id>
    <url>http://jcenter.bintray.com</url>
</repository>
```

Add dependencies:

```xml
<dependency>
  <groupId>com.uchuhimo</groupId>
  <artifactId>kotlinx-bimap</artifactId>
  <version>0.4</version>
  <type>pom</type>
</dependency>
```

### Gradle

Add Bintray JCenter repository:

```groovy
repositories {
    jcenter()
}
```

Add dependencies:

```groovy
compile 'com.uchuhimo:kotlinx-bimap:0.4'
```

## Building from source

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
