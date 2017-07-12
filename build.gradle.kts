import com.diffplug.gradle.spotless.SpotlessExtension
import java.util.Properties
import org.junit.platform.gradle.plugin.FiltersExtension
import org.junit.platform.gradle.plugin.EnginesExtension
import org.junit.platform.gradle.plugin.JUnitPlatformExtension
import com.novoda.gradle.release.PublishExtension
import io.spring.gradle.dependencymanagement.dsl.DependencyManagementConfigurer
import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import io.spring.gradle.dependencymanagement.internal.dsl.StandardDependencyManagementExtension
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        //maven { url = java.net.URI("http://maven.aliyun.com/nexus/content/groups/public") }
        jcenter()
    }
    dependencies {
        classpath("org.junit.platform:junit-platform-gradle-plugin:1.0.0-M4")
        classpath("com.novoda:bintray-release:0.5.0")
        classpath("org.jetbrains.dokka:dokka-gradle-plugin:0.9.15")
    }
}

val junitPlatformVersion by extra("1.0.0-M4")

var bintrayUserProperty by extra("")
var bintrayKeyProperty by extra("")
if (project.rootProject.file("private.properties").exists()) {
    val properties = Properties()
    properties.load(project.rootProject.file("private.properties").inputStream())
    bintrayUserProperty = properties.getProperty("bintrayUser")
    bintrayKeyProperty = properties.getProperty("bintrayKey")
}

plugins {
    `build-scan`
    java
    kotlin("jvm") version "1.1.3-2"
    id("com.dorongold.task-tree") version "1.3"
    id("com.diffplug.gradle.spotless") version "3.4.0"
    id("io.spring.dependency-management") version "1.0.3.RELEASE"
}

apply {
    plugin("org.junit.platform.gradle.plugin")
    plugin("com.novoda.bintray-release")
    plugin("org.jetbrains.dokka")
}

group = "com.uchuhimo"
version = "0.3"

repositories {
    //maven { url = java.net.URI("http://maven.aliyun.com/nexus/content/groups/public") }
    jcenter()
}

val wrapper by tasks.creating(Wrapper::class) {
    distributionUrl = "https://repo.gradle.org/gradle/dist-snapshots/gradle-kotlin-dsl-4.1-20170707032407+0000-all.zip"
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_6
    targetCompatibility = JavaVersion.VERSION_1_6
}

configure<JUnitPlatformExtension> {
    filters {
        engines {
            include("spek")
        }
    }
}

// extension for configuration
fun JUnitPlatformExtension.filters(setup: FiltersExtension.() -> Unit) {
    when (this) {
        is ExtensionAware -> extensions.getByType(FiltersExtension::class.java).setup()
        else -> throw Exception("${this::class} must be an instance of ExtensionAware")
    }
}

fun FiltersExtension.engines(setup: EnginesExtension.() -> Unit) {
    when (this) {
        is ExtensionAware -> extensions.getByType(EnginesExtension::class.java).setup()
        else -> throw Exception("${this::class} must be an instance of ExtensionAware")
    }
}

configure<DependencyManagementExtension> {
    dependencies {
        dependency("com.google.guava:guava:22.0")

        dependencySet("org.jetbrains.kotlin:1.1.3-2") {
            entry("kotlin-stdlib")
            entry("kotlin-reflect")
            entry("kotlin-test")
        }
    }

    manage(configurations.testImplementation) {
        dependencies {
            dependency("com.natpryce:hamkrest:1.4.1.0")
            dependency("org.hamcrest:hamcrest-all:1.3")

            dependency("org.junit.platform:junit-platform-launcher:$junitPlatformVersion")

            dependencySet("org.jetbrains.spek:1.1.2") {
                entry("spek-api")
                entry("spek-data-driven-extension")
                entry("spek-subject-extension")
                entry("spek-junit-platform-engine")
            }
        }
    }
}

fun DependencyManagementExtension.manage(
        configuration: Configuration,
        handler: DependencyManagementConfigurer.() -> Unit) {
    this as StandardDependencyManagementExtension
    methodMissing(configuration.name, arrayOf(delegateClosureOf(handler)))
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("com.google.guava:guava")

    testImplementation("org.jetbrains.kotlin:kotlin-reflect")
    testImplementation("com.natpryce:hamkrest")
    testImplementation("org.jetbrains.spek:spek-api")
    testImplementation("org.jetbrains.spek:spek-data-driven-extension")
    testImplementation("org.jetbrains.spek:spek-subject-extension")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testRuntimeOnly("org.jetbrains.spek:spek-junit-platform-engine")
}

tasks.withType(JavaCompile::class.java) {
    options.encoding = "UTF-8"
}

tasks.withType(Test::class.java) {
    testLogging.showStandardStreams = true
}

tasks.withType(KotlinCompile::class.java) {
    kotlinOptions {
        jvmTarget = "1.6"
        apiVersion = "1.1"
        languageVersion = "1.1"
    }
}

configure<SpotlessExtension> {
    kotlin {
        ktlint("0.8.3")
        trimTrailingWhitespace()
        endWithNewline()
    }
}

tasks {
    "dokka"(DokkaTask::class) {
        outputFormat = "html"
        outputDirectory = "javadoc"(Javadoc::class).destinationDir.path
    }
}

configure<PublishExtension> {
    userOrg = "uchuhimo"
    groupId = project.group as String
    artifactId = rootProject.name
    publishVersion = project.version as String
    setLicences("Apache-2.0")
    desc = "a bidirectional map implementation for Kotlin"
    website = "https://github.com/uchuhimo/kotlinx-bimap"
    bintrayUser = bintrayUserProperty
    bintrayKey = bintrayKeyProperty
    dryRun = false
}

afterEvaluate {
    tasks {
        "mavenJavadocJar" {
            dependsOn("dokka")
        }
        "bintrayUpload" {
            dependsOn("jar", "mavenJavadocJar", "mavenSourcesJar", "check")
        }
    }
}

tasks {
    val install by creating
    whenObjectAdded {
        if (name == "generatePomFileForMavenPublication") {
            "bintrayUpload" {
                dependsOn(this)
            }
        } else if (name == "publishToMavenLocal") {
            install.dependsOn(this)
        }
    }
}

buildScan {
    setLicenseAgreementUrl("https://gradle.com/terms-of-service")
    setLicenseAgree("yes")
}
