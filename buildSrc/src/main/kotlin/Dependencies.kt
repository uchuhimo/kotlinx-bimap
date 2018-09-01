import org.gradle.api.JavaVersion

object Versions {
    val java = JavaVersion.VERSION_1_6
    val kotlin = "1.2.61"
    val kotlinApi = "1.2"
    val junit = "5.2.0"
    val junitPlatform = "1.2.0"
    val spek = "1.2.0"
    val bintrayPlugin = "1.8.4"
    val bintrayRelease = "0.8.1"
    val taskTree = "1.3"
    val spotless = "3.14.0"
    val dependencyManagement = "1.0.6.RELEASE"
    val dependencyUpdate = "0.20.0"
    val dokka = "0.9.17"
    val hamkrest = "1.6.0.0"
    val ktlint = "0.24.0"
    val jacoco = "0.8.1"
    // 20.0 is the last release that supports JDK 1.6
    val guava = "20.0"
}

fun String?.withColon() = this?.let { ":$this" } ?: ""

fun kotlin(module: String, version: String? = null) =
    "org.jetbrains.kotlin:kotlin-$module${version.withColon()}"

fun spek(module: String, version: String? = null) =
    "org.jetbrains.spek:spek-$module${version.withColon()}"

fun junit(scope: String, module: String, version: String? = null) =
    "org.junit.$scope:junit-$scope-$module${version.withColon()}"
