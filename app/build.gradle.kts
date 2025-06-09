plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
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
            buildConfigField("String", "BACKEND_BASE_URL", "\"https://medquick-backend-app-953862767231.us-central1.run.app\"")
        }

        debug {
            buildConfigField("String", "BACKEND_BASE_URL", "\"https://medquick-backend-app-953862767231.us-central1.run.app\"")
        }
    }

    buildFeatures {
        buildConfig = true

        // ✅ Se habilita ViewBinding para permitir acceso seguro a los elementos de los layouts
        // sin necesidad de usar findViewById() manualmente.
        viewBinding = true
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

    // ✅ Retrofit para peticiones HTTP
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // ✅ Socket.IO si usas sockets para comunicación en tiempo real
    implementation("io.socket:socket.io-client:2.1.0")

    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("com.squareup.okio:okio:3.5.0")

    // ✅ Firebase (Analytics y Mensajería)
    implementation(platform("com.google.firebase:firebase-bom:33.13.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-messaging:23.4.1")
    implementation(libs.swiperefreshlayout)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Material Design Components (REQUERIDO para los chips y nuevos componentes)
    implementation("com.google.android.material:material:1.10.0")

    // AndroidX Core (para compatibilidad)
    implementation("androidx.core:core:1.10.0")

    // Constraint Layout (si no lo tienes)
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Coordinator Layout (para el FAB y comportamientos)
    implementation("androidx.coordinatorlayout:coordinatorlayout:1.2.0")

    // RecyclerView (si no está incluido)
    implementation("androidx.recyclerview:recyclerview:1.3.1")

    // CardView (si no está incluido)
    implementation("androidx.cardview:cardview:1.0.0")
}
