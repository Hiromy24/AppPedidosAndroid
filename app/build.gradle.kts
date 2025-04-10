plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.apppedidosandroid"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.apppedidosandroid"
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
}

dependencies {
    implementation(libs.firebase.database)
    implementation (libs.mysql.connector.java)
    implementation (libs.com.github.bumptech.glide.glide)
    implementation(libs.swiperefreshlayout)
    annotationProcessor (libs.compiler)
    implementation (libs.retrofit)
    implementation (libs.converter.gson)
    implementation (libs.squareup.logging.interceptor)
    implementation(libs.glide)
    implementation(libs.google.firebase.auth)
    annotationProcessor(libs.compiler)
    implementation(libs.play.services.auth)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}