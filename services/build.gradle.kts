plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "co.japl.android.synapsefit.services"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(project(":core"))
    implementation(project(":util"))

    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    implementation(libs.security.crypto)
    implementation(libs.kotlinx.coroutines.core)

    testImplementation(libs.junit)
}
