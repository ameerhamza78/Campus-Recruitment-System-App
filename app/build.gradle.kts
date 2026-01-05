plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

// ** THE FINAL FIX **
// This block must be at the top level. It tells Gradle to exclude
// the old, conflicting protobuf-lite library from ALL configurations.
configurations.all {
    exclude(group = "com.google.protobuf", module = "protobuf-lite")
}

android {
    namespace = "com.example.crs2025"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.crs2025"
        minSdk = 24
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
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Firebase dependencies using the Bill of Materials (BOM)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.database)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    
    // Splash Screen library
    implementation("androidx.core:core-splashscreen:1.0.1")

    // Testing libraries
    implementation(libs.espresso.idling.resource)
    implementation(libs.ext.junit)
    implementation(libs.espresso.contrib)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.espresso.intents)
    androidTestImplementation(libs.core)
    androidTestImplementation(libs.rules)
    
    // No longer need any manual protobuf management here.
}