plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.kapt) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.detekt) apply false
    id("com.google.gms.google-services") version "4.5.0" apply false
    alias(libs.plugins.google.firebase.crashlytics) apply false
}

abstract class CopyGoogleServicesTask : DefaultTask() {
    @get:Input
    @get:Optional
    abstract val sourceFilePath: Property<File>

    @get:OutputFile
    abstract val targetFile: RegularFileProperty

    @TaskAction
    fun copy() {
        val srcPath = sourceFilePath.orNull
        val dest = targetFile.get().asFile

        if (!dest.exists() && srcPath != null && srcPath.exists()) {
            srcPath.copyTo(dest, overwrite = true)
            logger.lifecycle("--> [Build Local] [${project.name}] google-services.json copiado exitosamente.")
        } else {
            logger.lifecycle("--> [Build Local] [${project.name}] google-services.json No fue encontrado o ya existe.")
        }
    }
}

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    apply(plugin = "io.gitlab.arturbosch.detekt")

    configurations.configureEach {
        resolutionStrategy.force("org.jetbrains.kotlin:kotlin-stdlib:2.0.20")
    }

    configure<io.gitlab.arturbosch.detekt.extensions.DetektExtension> {
        buildUponDefaultConfig = true
    }

    tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
        jvmTarget = "17"
        config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
    }

    plugins.withId("com.google.gms.google-services") {
        val copyGoogleServicesJson =
            tasks.register<CopyGoogleServicesTask>("copyGoogleServicesJson") {
                description = "Copia el archivo google-services.json si no existe localmente."

                val localProperties = java.util.Properties()
                val localPropertiesFile = rootProject.file("local.properties")
                if (localPropertiesFile.exists()) {
                    localPropertiesFile.inputStream().use { localProperties.load(it) }
                }
                val googleServicesPath =
                    localProperties.getProperty("google.services.path") ?: "google-services.json"

                val externalFile = rootProject.file(googleServicesPath)
                val target = project.layout.projectDirectory.file("google-services.json")
                sourceFilePath.set(externalFile)
                targetFile.set(target)

                onlyIf {
                    !target.asFile.exists()
                }
            }

        tasks.configureEach {
            if ((name.startsWith("process") && name.endsWith("GoogleServices")) || name == "preBuild") {
                dependsOn(copyGoogleServicesJson)
            }
        }
    }
}
