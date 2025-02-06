pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.google.com")
        maven("https://oss.sonatype.org/content/repositories/ksoap2-android-releases/")
        maven("https://jitpack.io")
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)
    repositories {
        google()
        mavenCentral()
        maven("https://maven.google.com")
        maven("https://oss.sonatype.org/content/repositories/ksoap2-android-releases/")
        maven("https://jitpack.io")
        flatDir {
            dirs("libs")
        }
    }
}

rootProject.name = "My Application"
include(":app")

include(":app2")
