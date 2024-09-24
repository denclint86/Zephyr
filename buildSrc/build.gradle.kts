plugins {
    id("maven-publish")
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    google()
    gradlePluginPortal()
}

//afterEvaluate {
//    publishing {
//        publications {
//            create<MavenPublication>("maven_public") {
//                from(components["release"])
//
//                groupId = BuildSource.USER_FILED
//                artifactId = BuildSource.DEV
//                version = BuildSource.PUBLISH_VERSION
//            }
//        }
//    }
//}