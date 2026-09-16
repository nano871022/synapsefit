abstract class CopyGoogleServicesTask : DefaultTask() {
    @get:InputFile
    @get:Optional
    abstract val sourceFile: RegularFileProperty

    @get:OutputFile
    abstract val targetFile: RegularFileProperty

    @TaskAction
    fun copy() {
        val src = sourceFile.orNull?.asFile
        val dest = targetFile.get().asFile

        if (!dest.exists() && src != null && src.exists()) {
            src.copyTo(dest, overwrite = true)
            logger.lifecycle("--> [Build Local] google-services.json copiado exitosamente.")
        }
    }
}

val copyGoogleServicesJson =
    tasks.register<CopyGoogleServicesTask>("copyGoogleServicesJson") {
    description = "Copia el archivo google-services.json si no existe localmente."
    // Cambia la ruta según la ubicación de tu repositorio externo
    sourceFile.set(layout.projectDirectory.file("../../japl-properties/synapseefit/google-services.json"))
    targetFile.set(layout.projectDirectory.file("google-services.json"))
}

tasks.configureEach {
    if ((name.startsWith("process") && name.endsWith("GoogleServices")) || name == "preBuild") {
        dependsOn(copyGoogleServicesJson)
    }
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    id("com.google.gms.google-services")
}

android {
    namespace = "co.japl.android.synapsefit"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "co.japl.android.synapsefit"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 11_00_010
        versionName = "1.00.010 Integracion entre wear y app"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(project(":core"))
    implementation(project(":services"))
    implementation(project(":ui"))
    implementation(project(":util"))
    if (findProject(":about") != null) {
        implementation(project(":about"))
    }

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.fragment.ktx)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)

    implementation(libs.room.runtime)
    implementation(libs.room.ktx)

    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    debugImplementation(libs.compose.ui.tooling)
    implementation(libs.compose.material3)
    implementation(libs.compose.material3.window.size)
    implementation(libs.compose.material.icons.extended)
    implementation(libs.play.services.wearable)

    testImplementation(project(":services:wear"))
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
}
