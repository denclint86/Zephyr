plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.zephyr.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.zephyr.demo"
        minSdk = 26
        targetSdk = 34
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
    implementation(project(":datastore"))
    implementation(project(":extension"))
    implementation(project(":global-values"))
    implementation(project(":log"))
    implementation(project(":net"))
    implementation(project(":scaling-layout"))
    implementation(project(":vbclass"))

//    implementation("androidx.compose.runtime:runtime:1.7.8") // 改成自己的那些
    implementation(libs.material)
}