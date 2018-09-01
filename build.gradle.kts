import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import com.novoda.gradle.release.PublishExtension
import io.spring.gradle.dependencymanagement.dsl.DependenciesHandler
import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import io.spring.gradle.dependencymanagement.dsl.DependencySetHandler
import org.jetbrains.dokka.DokkaConfiguration
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.dokka.gradle.LinkMapping
import org.jetbrains.kotlin.gradle.dsl.Coroutines
import java.util.Properties
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.net.URL

val bintrayUserProperty by extra { getPrivateProperty("bintrayUser") }
val bintrayKeyProperty by extra { getPrivateProperty("bintrayKey") }

buildscript {
    repositories {
        aliyunMaven()
        jcenter()
        maven(url = "https://dl.bintray.com/kotlin/kotlin-eap")
    }
    dependencies {
        classpath("com.jfrog.bintray.gradle:gradle-bintray-plugin:${Versions.bintrayPlugin}")
        classpath("com.novoda:bintray-release:${Versions.bintrayRelease}")
    }
}

plugins {
    `build-scan`
    java
    jacoco
    kotlin("jvm") version Versions.kotlin
    id("com.dorongold.task-tree") version Versions.taskTree
    id("com.diffplug.gradle.spotless") version Versions.spotless
    id("com.github.ben-manes.versions") version Versions.dependencyUpdate
    id("io.spring.dependency-management") version Versions.dependencyManagement
    id("org.jetbrains.dokka") version Versions.dokka
}

apply(plugin = "com.novoda.bintray-release")
apply(plugin = "com.jfrog.bintray")

group = "com.uchuhimo"
version = "1.0"

repositories {
    aliyunMaven()
    jcenter()
}

val wrapper by tasks.registering(Wrapper::class)
wrapper {
    gradleVersion = "4.10"
    distributionType = Wrapper.DistributionType.ALL
}

configure<DependencyManagementExtension> {
    dependencies {
        dependency(kotlin("stdlib", Versions.kotlin))
        dependency("com.google.guava:guava:${Versions.guava}")
    }

    val testImplementation by configurations
    testImplementation.withDependencies {
        dependencies {
            dependency(kotlin("test", Versions.kotlin))
            dependency("com.natpryce:hamkrest:${Versions.hamkrest}")

            dependency(junit("platform", "launcher", Versions.junitPlatform))

            arrayOf("api", "data-driven-extension", "subject-extension", "junit-platform-engine").forEach { name ->
                dependency(spek(name, Versions.spek))
            }
        }
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.google.guava:guava")

    testImplementation("com.natpryce:hamkrest")
    arrayOf("api", "data-driven-extension", "subject-extension").forEach { name ->
        testImplementation(spek(name))
    }

    testRuntimeOnly(junit("platform", "launcher"))
    testRuntimeOnly(spek("junit-platform-engine"))
}

java {
    sourceCompatibility = Versions.java
    targetCompatibility = Versions.java
}

val test by tasks.existing(Test::class)
test {
    useJUnitPlatform()
    testLogging.showStandardStreams = true
}

if (System.getenv().containsKey("JDK7_HOME")) {
    val Jdk7Home = System.getenv()["JDK7_HOME"]
    tasks.withType<JavaCompile> {
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
    tasks.withType<KotlinCompile> {
        println("$name: use JDK7 to compile")
        kotlinOptions.jdkHome = System.getenv()["JDK7_HOME"]
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = Versions.java.toString()
        apiVersion = Versions.kotlinApi
        languageVersion = Versions.kotlinApi
    }
}

spotless {
    kotlin {
        ktlint(Versions.ktlint)
        trimTrailingWhitespace()
        endWithNewline()
        // licenseHeaderFile is unstable for Kotlin
        // (i.e. will remove `@file:JvmName` when formatting), disable it by default
        //licenseHeaderFile rootProject.file("config/spotless/apache-license-2.0.kt")
    }
}

jacoco {
    toolVersion = Versions.jacoco
}

val jacocoTestReport by tasks.existing(JacocoReport::class) {
    reports {
        xml.isEnabled = true
        html.isEnabled = true
    }
}

val check by tasks.existing {
    dependsOn(jacocoTestReport)
}

val dokka by tasks.existing(DokkaTask::class) {
    outputFormat = "html"
    val javadoc: Javadoc by tasks
    outputDirectory = javadoc.destinationDir!!.path
    jdkVersion = 6
    linkMapping(delegateClosureOf<LinkMapping> {
        dir = "src/main/kotlin"
        url = "https://github.com/uchuhimo/konf/blob/v${project.version}/src/main/kotlin"
        suffix = "#L"
    })
    externalDocumentationLink(delegateClosureOf<DokkaConfiguration.ExternalDocumentationLink.Builder> {
        url = URL("https://google.github.io/guava/releases/${Versions.guava}/api/docs/")
    })
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

tasks {
    val install by registering
    afterEvaluate {
        val mavenJavadocJar by existing
        val publishToMavenLocal by existing
        val bintrayUpload by existing
        mavenJavadocJar { dependsOn(dokka) }
        install.configure { dependsOn(publishToMavenLocal) }
        bintrayUpload { dependsOn(check, install) }
    }
}

val dependencyUpdates by tasks.existing(DependencyUpdatesTask::class)
dependencyUpdates {
    revision = "release"
    outputFormatter = "plain"
}

buildScan {
    setTermsOfServiceUrl("https://gradle.com/terms-of-service")
    setTermsOfServiceAgree("yes")
}
