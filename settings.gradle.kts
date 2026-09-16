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

fun configureAboutProject(aboutFolder: File, buildFileName: String) {
    include(":about")
    project(":about").projectDir = aboutFolder
    project(":about").buildFileName = buildFileName
}

val localAboutFolder = file("../japl-android-about-module")
val actionsAboutFolder = file("about")
when {
    localAboutFolder.exists() -> configureAboutProject(
        localAboutFolder,
        "../synapsefit/about-consumer.gradle.kts",
    )
    actionsAboutFolder.exists() -> configureAboutProject(
        actionsAboutFolder,
        "../about-consumer.gradle.kts",
    )
    else -> {
        include(:about)
    }
}
