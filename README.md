# kotlinx-bimap

[![Java 6+](https://img.shields.io/badge/Java-6+-4c7e9f.svg)](http://java.oracle.com)
[![Bintray](https://api.bintray.com/packages/uchuhimo/maven/kotlinx-bimap/images/download.svg)](https://bintray.com/uchuhimo/maven/kotlinx-bimap/_latestVersion)
[![JitPack](https://jitpack.io/v/uchuhimo/kotlinx-bimap.svg)](https://jitpack.io/#uchuhimo/kotlinx-bimap)
[![Build Status](https://travis-ci.org/uchuhimo/kotlinx-bimap.svg?branch=master)](https://travis-ci.org/uchuhimo/kotlinx-bimap)
[![codecov](https://codecov.io/gh/uchuhimo/kotlinx-bimap/branch/master/graph/badge.svg)](https://codecov.io/gh/uchuhimo/kotlinx-bimap)

A bimap (bidirectional map) implementation for Kotlin.

## Prerequisites

- JDK 1.6 or higher

## Interfaces and implementations

This library provides interfaces for mutable/immutable bimap:

| Interface | Bases | Implementations |
| - | - | - |
| `BiMap` | `Map` | `emptyBiMap`, `biMapOf`, `toBiMap` |
| `MutableBiMap` | `MutableMap`, `BiMap` | `mutableBiMapOf`, `toMutableBiMap` |

## Operations

### Create read-only bimap

- Create an empty read-only bimap:

  ```kotlin
val newBiMap = emptyBiMap()
```

- Create a new read-only bimap from pairs:

  ```kotlin
val newBiMap = biMapOf(1 to "1", 2 to "2", 3 to "3")
```

- Create a new read-only bimap from map:

  ```kotlin
val newBiMap = mapOf(1 to "1", 2 to "2", 3 to "3").toBiMap()
```

### Create mutable bimap

- Create an empty mutable bimap:

  ```kotlin
val newBiMap = mutableBiMapOf()
```

- Create a new mutable bimap from pairs:

  ```kotlin
val newBiMap = mutableBiMapOf(1 to "1", 2 to "2", 3 to "3")
```

- Create a new mutable bimap from map:

  ```kotlin
val newBiMap = mapOf(1 to "1", 2 to "2", 3 to "3").toMutableBiMap()
```

### Query bimap

- Bimap support all operations of map:

  ```kotlin
val biMap = biMapOf(1 to "1", 2 to "2", 3 to "3")
biMap.size // 3
biMap.isEmpty() // false
biMap.values // ["1", "2", "3"]
biMap[1] // "1"
biMap.containsKey(4) // false
biMap.getOrDefault(4, "4") // "4"
```

- Get the inverse view of bimap:

  ```kotlin
val biMap = biMapOf(1 to "1", 2 to "2", 3 to "3")
val inverseBiMap = biMap.inverse
inverseBiMap.values // [1, 2, 3]
```

### Mutate mutable bimap

Mutable bimap support all operations of mutable map:

```kotlin
val mutableBiMap = mutableBiMapOf(1 to "1", 2 to "2", 3 to "3")
mutableBiMap[3] = "4"
mutableBiMap.remove(1)
mutableBiMap.clear()
```

When using `put` operation (i.e., `mutableBiMap[3] = "4"`), the bimap throws `IllegalArgumentException` if the given value is already bound to a different key in it. The bimap will remain unmodified in this event. To avoid this exception, call `forcePut` instead:

```kotlin
mutableBiMap.forcePut(4, "2")
```

The `forcePut` operation will silently remove any existing entry with the value before proceeding with the `put` operation.

### Interoperability with Guava bimap

- Views a mutable bimap as a Guava bimap:

  ```kotlin
val mutableBiMap = mutableBiMapOf(1 to "1", 2 to "2", 3 to "3")
val guavaBiMap = mutableBiMap.asGuavaBiMap()
```

- Views a Guava bimap as a mutable bimap:

  ```kotlin
val guavaBiMap = HashBiMap.create(mapOf(1 to "1", 2 to "2", 3 to "3"))
val mutableBiMap = guavaBiMap.asMutableBiMap()
```

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
