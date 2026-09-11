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
