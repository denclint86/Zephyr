plugins {
    id("maven-publish")
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.p1ay1s.dev"
    compileSdk = BuildSource.COMPILE_SDK

    defaultConfig {
        minSdk = BuildSource.MIN_SDK

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildFeatures {
        dataBinding = true
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = BuildSource.JDK_VERSION
        targetCompatibility = BuildSource.JDK_VERSION
    }
    kotlinOptions {
        jvmTarget = BuildSource.JVM_TARGET
    }
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("maven_public") {
                from(components["release"])

                groupId = BuildSource.USER_FILED
                artifactId = BuildSource.DEV
                version = BuildSource.PUBLISH_VERSION
            }
        }
    }
}

dependencies {
    implementation(libs.github.glide)

    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    implementation(libs.androidx.recyclerview)

    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    implementation(libs.material)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}