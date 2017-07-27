import com.diffplug.gradle.spotless.SpotlessExtension
import java.util.Properties
import java.net.URL
import org.junit.platform.gradle.plugin.FiltersExtension
import org.junit.platform.gradle.plugin.EnginesExtension
import org.junit.platform.gradle.plugin.JUnitPlatformExtension
import com.novoda.gradle.release.PublishExtension
import groovy.lang.Closure
import io.spring.gradle.dependencymanagement.dsl.DependencyManagementConfigurer
import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import io.spring.gradle.dependencymanagement.internal.dsl.StandardDependencyManagementExtension
import org.jetbrains.dokka.DokkaConfiguration
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.dokka.gradle.LinkMapping
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
    jacoco
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
version = "0.4"

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
        // 20.0 is the last release that supports JDK 1.6
        dependency("com.google.guava:guava:20.0")

        dependencySet("org.jetbrains.kotlin:1.1.3-2") {
            entry("kotlin-stdlib")
            entry("kotlin-reflect")
        }
    }

    manage(configurations.testImplementation) {
        dependencies {
            dependency("org.jetbrains.kotlin:kotlin-test:1.1.3-2")

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

if (System.getenv().containsKey("JDK7_HOME")) {
    val Jdk7Home = System.getenv()["JDK7_HOME"]
    tasks.withType(JavaCompile::class.java) {
        println("$name: use JDK7 to compile")
        options.apply {
            isFork = true
            bootClasspath = "$Jdk7Home/jre/lib/rt.jar"
            forkOptions.apply {
                javaHome = File("$Jdk7Home/jre")
                executable = "$Jdk7Home/bin/javac"
            }
        }
    }
    tasks.withType(KotlinCompile::class.java) {
        println("$name: use JDK7 to compile")
        kotlinOptions.jdkHome = System.getenv()["JAVA_HOME"]
    }
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
        // licenseHeaderFile is unstable for Kotlin
        // (i.e. will remove `@file:JvmName` when formatting), disable it by default
        //licenseHeaderFile(rootProject.file("config/spotless/apache-license-2.0.kt"))
    }
}

afterEvaluate {
    tasks {
        val junitPlatformTest = "junitPlatformTest"(JavaExec::class)
        configure<JacocoPluginExtension> {
            toolVersion = "0.7.9"
            applyTo(junitPlatformTest)
        }
        val jacocoJunitPlatformReport by creating(JacocoReport::class) {
            executionData(junitPlatformTest)
            sourceSets(java.sourceSets["main"])
            sourceDirectories = files(java.sourceSets["main"].allSource.srcDirs)
            classDirectories = files(java.sourceSets["main"].output)
            reports {
                xml.isEnabled = true
                html.isEnabled = true
            }
        }
        "check"().dependsOn(jacocoJunitPlatformReport)
    }
}

tasks {
    "dokka"(DokkaTask::class) {
        outputFormat = "html"
        outputDirectory = "javadoc"(Javadoc::class).destinationDir.path
        jdkVersion = 6
        linkMapping(delegateAnyClosureOf<LinkMapping> {
            dir = project.rootDir.toPath().resolve("src/main/kotlin").toFile().path
            url = "https://github.com/uchuhimo/kotlinx-bimap/blob/v${project.version}/src/main/kotlin"
            suffix = "#L"
        })
        externalDocumentationLink(
                delegateAnyClosureOf<DokkaConfiguration.ExternalDocumentationLink.Builder> {
                    url = URL("https://google.github.io/guava/releases/20.0/api/docs/")
                })
    }
}

fun <T> Any.delegateAnyClosureOf(action: T.() -> Unit) =
        object : Closure<Any?>(this, this) {
            @Suppress("UNCHECKED_CAST")
            fun doCall() = (delegate as T).action()
        }

configure<PublishExtension> {
    userOrg = "uchuhimo"
    groupId = project.group as String
    artifactId = rootProject.name
    publishVersion = project.version as String
    setLicences("Apache-2.0")
    desc = "a bimap (bidirectional map) implementation for Kotlin"
    website = "https://github.com/uchuhimo/kotlinx-bimap"
    bintrayUser = bintrayUserProperty
    bintrayKey = bintrayKeyProperty
    dryRun = false
}

afterEvaluate {
    tasks {
        "mavenJavadocJar"().dependsOn("dokka")
        "bintrayUpload"().dependsOn("jar", "mavenJavadocJar", "mavenSourcesJar", "check")
    }
}

tasks {
    val install by creating
    whenObjectAdded {
        if (name == "generatePomFileForMavenPublication") {
            "bintrayUpload"().dependsOn(this@whenObjectAdded)
        } else if (name == "publishToMavenLocal") {
            install.dependsOn(this)
        }
    }
}

buildScan {
    setLicenseAgreementUrl("https://gradle.com/terms-of-service")
    setLicenseAgree("yes")
}
