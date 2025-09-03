// 16/03/25: Endrer CompileSdk fra 34 til 35

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)


}

android {
    namespace = "no.uio.ifi.in2000.team39"
    compileSdk = 35

    defaultConfig {
        applicationId = "no.uio.ifi.in2000.team39"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation (libs.mpandroidchart)

    implementation(libs.vico.compose)
    implementation(libs.vico.compose.m2)
    implementation(libs.vico.compose.m3)
    implementation(libs.vico.multiplatform)
    implementation(libs.vico.views)
    implementation(libs.vico.core)



    //implementation("com.patrykandpatrick.vico:compose:2.1.2")
    //implementation("com.patrykandpatrick.vico:compose-m3:2.1.2")
    //implementation("com.patrykandpatrick.vico:core:2.1.2")

    //implementation(libs.vico.compose)       // For Compose charts
    //implementation(libs.vico.compose.m3)   // For Material 3 support
    //implementation(libs.vico.core)         // Core VICO functionality




    implementation(platform(libs.androidx.compose.bom))  // 2024.04.01 is fine


    implementation(libs.compose.ui.text) // Dette er for at når du trykker Enter, skal du ikke få linjeskift. Egt for pc support kan fjernes etter debugging?

    // Ktor
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.kotlinx.serialization.json)
    testImplementation(libs.ktor.client.mock)


    // MapLibre
    implementation(libs.maplibre.sdk)
    implementation(libs.compose.ui)
    implementation(libs.compose.material)

    // Mapbox GeoJSON
    //implementation(libs.mapbox.geojson)



    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.vision.internal.vkp)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.navigation.runtime.ktx)

    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.common.jvm)
    implementation(libs.androidx.room.runtime.android)


    implementation(libs.androidx.adapters)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    testImplementation(libs.mockk)


    // Dagger Hilt

    implementation(libs.dagger.hilt.android)
    ksp(libs.hilt.android.compiler)
    implementation(libs.androidx.hilt.navigation.fragment)
    implementation(libs.androidx.hilt.navigation.compose)
    kspTest(libs.hilt.android.compiler)
    androidTestImplementation(libs.hilt.android.testing)
    kspAndroidTest(libs.hilt.android.compiler)

    implementation(libs.slf4j.simple)
}