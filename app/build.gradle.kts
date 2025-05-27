plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.zephyr.demo"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.zephyr.demo"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(libs.material)

    implementation(project(":vbclass"))
    implementation(project(":log"))
//    implementation(libs.zephyr.vbclass)
//    implementation(libs.zephyr.scaling.layout)
//    implementation(libs.zephyr.net)
//    implementation(libs.zephyr.log)
//    implementation(libs.zephyr.extension)
}