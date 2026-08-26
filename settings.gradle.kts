pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
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
