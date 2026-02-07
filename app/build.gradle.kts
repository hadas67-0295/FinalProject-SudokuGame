import java.util.Properties
plugins {
    alias(libs.plugins.android.application)
}
val localProps = Properties().apply {
    load(rootProject.file("local.properties").inputStream())
}
val apiKey = localProps.getProperty("GOOGLE_API_KEY") ?: ""

android {
    namespace = "com.example.finalproject_sudokugame"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.finalproject_sudokugame"
        minSdk = 34
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        packaging {
            resources {
                excludes += "/META-INF/INDEX.LIST"
                excludes += "/META-INF/DEPENDENCIES"
                excludes += "/META-INF/AL2.0"
                excludes += "/META-INF/LGPL2.1"
                excludes += "/META-INF/NOTICE*"
                excludes += "/META-INF/LICENSE*"
            }
        }
        buildConfigField("String", "GOOGLE_API_KEY", "\"$apiKey\"")
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

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(libs.room.runtime)
    implementation("com.google.genai:google-genai:1.24.0")
    annotationProcessor(libs.room.compiler)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
