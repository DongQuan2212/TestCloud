plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "hcmute.edu.vn.could"
    compileSdk = 35

    defaultConfig {
        applicationId = "hcmute.edu.vn.could"
        minSdk = 28
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
    
    // Cloudinary SDK
    implementation (libs.cloudinary.android)
    
    // Glide for image loading
    implementation(libs.glide)
    
    // Retrofit for network calls
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}