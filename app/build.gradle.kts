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

        buildConfigField("String", "BACKEND_BASE_URL", "\"http://localhost:3000\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            //Configuracion para ip de produccion
            //buildConfigField("String", "BACKEND_BASE_URL", project.properties["backendBaseUrlRelease"] as? String ?: "\"http://ip_produccion:3000\"")
        }
        debug {
            // Agregar configuraciones específicas para debug aquí si es necesario.
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
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}