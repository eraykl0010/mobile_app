plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.pdks.mobile"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.pdks.mobile"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
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

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // AndroidX Core
    implementation("androidx.core:core-ktx:1.16.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.activity:activity:1.10.1")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")

    // Material Design
    implementation("com.google.android.material:material:1.12.0")

    // Network — Retrofit + Gson
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // CameraX — QR okuma için
    implementation("androidx.camera:camera-core:1.4.2")
    implementation("androidx.camera:camera-camera2:1.4.2")
    implementation("androidx.camera:camera-lifecycle:1.4.2")
    implementation("androidx.camera:camera-view:1.4.2")

    // ML Kit Barcode — QR okuma
    implementation("com.google.mlkit:barcode-scanning:17.3.0")

    // ZXing — QR kod üretimi
    implementation("com.google.zxing:core:3.5.3")

    // Location — Konum ile giriş/çıkış
    implementation("com.google.android.gms:play-services-location:21.3.0")

    // SharedPreferences — oturum yönetimi
    implementation("androidx.security:security-crypto:1.1.0-alpha06")

    // SwipeRefreshLayout
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    // ViewPager2
    implementation("androidx.viewpager2:viewpager2:1.1.0")

    // Testing — Unit
    testImplementation(libs.junit)
    testImplementation("com.google.truth:truth:1.4.4")
    testImplementation("org.mockito:mockito-core:5.14.2")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
    testImplementation("com.squareup.retrofit2:retrofit:2.11.0")
    testImplementation("com.squareup.retrofit2:converter-gson:2.11.0")
    testImplementation("com.google.code.gson:gson:2.11.0")

    // Testing — Instrumented (Android cihaz gerektirenler)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}