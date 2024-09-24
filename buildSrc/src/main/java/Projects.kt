import org.gradle.api.JavaVersion

object BuildSource {
    const val COMPILE_SDK = 34
    const val MIN_SDK = 24

    const val PUBLISH_VERSION = "2.5"
    const val EXTENSIONS = "extensions"
    const val DEV = "dev"
    const val USER_FILED = "com.github.p1ay1s"

    val JDK_VERSION = JavaVersion.VERSION_1_8
    const val JVM_TARGET = "1.8"
}