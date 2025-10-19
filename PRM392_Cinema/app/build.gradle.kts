plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.prm392_cinema"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.prm392_cinema"
        minSdk = 26
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}



dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.aar"))))

    // 🔽 Các thư viện bạn đã có
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation("com.squareup.okhttp3:okhttp:4.6.0")
    implementation("commons-codec:commons-codec:1.14")
    implementation("com.google.android.material:material:1.4.0")
    implementation("com.github.bumptech.glide:glide:4.12.0")
    implementation(libs.flexbox)
    implementation(libs.exoplayer)
    implementation(libs.picasso)

    // ⚠️ SDK ZaloPay thường không có trên Maven, bạn cần dùng file .aar
    // Nếu bạn tải được file .aar (vd: zalopay-release.aar), KHÔNG cần dòng dưới
    // Nếu ZaloPay đã cung cấp repository Maven riêng thì dùng dòng dưới thay thế
    // implementation("vn.zalopay.sdk:zalopay-sdk:latest.release")
}
