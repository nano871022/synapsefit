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
    api(project(":interfaces"))
    api(project(":core"))
    api(project(":util"))

    api(project(":services:feature"))
    api(project(":services:repository"))
    api(project(":services:gcp"))
    api(project(":services:llm"))
    api(project(":services:wear"))
}
