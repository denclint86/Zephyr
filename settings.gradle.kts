pluginManagement {
    repositories {
        maven {
            url = uri("https://www.jitpack.io")
            url = uri("https://maven.aliyun.com/repository")
            url = uri("https://maven.aliyun.com/repository/central")
            url = uri("https://maven.aliyun.com/repository/jcenter")
            url = uri("https://maven.aliyun.com/repository/google")
            url = uri("https://maven.aliyun.com/repository/gradle-plugin")
            url = uri("https://maven.aliyun.com/repository/public")
        }
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven {
            url = uri("https://www.jitpack.io")
            url = uri("https://maven.aliyun.com/repository")
            url = uri("https://maven.aliyun.com/repository/central")
            url = uri("https://maven.aliyun.com/repository/jcenter")
            url = uri("https://maven.aliyun.com/repository/google")
            url = uri("https://maven.aliyun.com/repository/gradle-plugin")
            url = uri("https://maven.aliyun.com/repository/public")
        }
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

rootProject.name = "Zephyr"
include(":dev")
include(":extensions")
//include(":app")
