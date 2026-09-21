pluginManagement {
    repositories {
        google()
        maven { url = uri("https://maven-central.storage-download.googleapis.com/maven2/") }
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        maven { url = uri("https://maven-central.storage-download.googleapis.com/maven2/") }
        gradlePluginPortal()
    }
}

rootProject.name = "SynapseFit"

val localProperties = java.util.Properties()
val localPropertiesFile = rootDir.resolve("local.properties")
if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use { localProperties.load(it) }
}

fun getLocalProperty(key: String): String? = localProperties.getProperty(key)

include(":app")
include(":core")
include(":services")
include(":services:feature")
include(":services:repository")
include(":services:gcp")
include(":services:llm")
include(":services:wear")
include(":ui")
include(":util")
include(":wear")

fun configureAboutProject(
    aboutFolder: File,
    buildFileName: String,
) {
    include(":about")
    project(":about").projectDir = aboutFolder
    project(":about").buildFileName = buildFileName
}

val aboutPath = getLocalProperty("about.path")
val aboutFolder = when {
    aboutPath != null && file(aboutPath).exists() -> file(aboutPath)
    file("../japl-android-about-module").exists() -> file("../japl-android-about-module")
    file("about").exists() -> file("about")
    else -> null
}

if (aboutFolder != null) {
    val isExternal = !aboutFolder.absolutePath.startsWith(rootDir.absolutePath)
    val buildFile = if (isExternal) {
        "../synapsefit/about-consumer.gradle.kts"
    } else {
        "../about-consumer.gradle.kts"
    }
    configureAboutProject(aboutFolder, buildFile)
} else {
    // Si no existe el folder, incluimos un módulo vacío para que findProject(":about") no sea nulo
    // y evitar errores críticos de Gradle, aunque la compilación Kotlin fallará si se usa.
    include(":about")
}
