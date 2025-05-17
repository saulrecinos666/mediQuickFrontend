plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.mediquick"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.mediquick"
        minSdk = 33
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
            buildConfigField("String", "BACKEND_BASE_URL", "\"http://ip_produccion:3000\"")
        }
        debug {
            // Detectar si es emulador o dispositivo físico
            val isEmulator = System.getenv("ANDROID_AVD_DEVICE") != null
            val backendUrl = if (isEmulator) {
                "http://10.0.2.2:3000"
            } else {
                "http://10.175.160.29:3000" // Cambia esta IP por tu IP local
            }

            buildConfigField("String", "BACKEND_BASE_URL", "\"$backendUrl\"")
        }
    }

    buildFeatures {
        buildConfig = true
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

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
