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

        //for hvakosterstrømmen api graf
        //JitPack lar oss hente biblioteker rett fra GitHub-repositorier,
        // (som for eksempel PhilJay/MPAndroidChart)
        //maven(url = "https://jitpack.io")
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // Dette trengs for å finne MPAndroidChart
        maven(url = "https://jitpack.io")
    }
}

rootProject.name = "Team39"
include(":app")
 