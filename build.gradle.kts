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

    @get:Input
    @get:Optional
    abstract val jsonContentEnv: Property<String>

    @get:OutputFile
    abstract val targetFile: RegularFileProperty

    @get:Input
    abstract val projectName: Property<String>

    @TaskAction
    fun copy() {
        val srcPath = sourceFilePath.orNull
        val rawEnv = jsonContentEnv.orNull
        val dest = targetFile.get().asFile
        val pName = projectName.get()

        if (!dest.exists()) {
            if (!rawEnv.isNullOrBlank()) {
                val trimmed = rawEnv.trim()
                val content = if (!trimmed.startsWith("{")) {
                    try {
                        String(java.util.Base64.getDecoder().decode(trimmed))
                    } catch (e: Exception) {
                        trimmed
                    }
                } else {
                    trimmed
                }
                dest.writeText(content)
                logger.lifecycle("--> [Build Local] [${pName}] google-services.json creado desde variable de entorno.")
            } else if (srcPath != null && srcPath.exists()) {
                srcPath.copyTo(dest, overwrite = true)
                logger.lifecycle("--> [Build Local] [${pName}] google-services.json copiado exitosamente desde ${srcPath.path}.")
            } else {
                logger.lifecycle("--> [Build Local] [${pName}] google-services.json no fue encontrado.")
            }
        } else {
            logger.lifecycle("--> [Build Local] [${pName}] google-services.json ya existe.")
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

                projectName.set(project.name)

                val localProperties = java.util.Properties()
                val localPropertiesFile = rootProject.file("local.properties")
                if (localPropertiesFile.exists()) {
                    localPropertiesFile.inputStream().use { localProperties.load(it) }
                }
                val googleServicesPath =
                    localProperties.getProperty("google.services.path") ?: "google-services.json"

                val externalFile = rootProject.file(googleServicesPath)
                val target = project.layout.projectDirectory.file("google-services.json")

                val envJson = System.getenv("GOOGLE_SERVICES_JSON") ?: System.getenv("GOOGLE_SERVICES_BASE64")

                sourceFilePath.set(externalFile)
                jsonContentEnv.set(envJson)
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
