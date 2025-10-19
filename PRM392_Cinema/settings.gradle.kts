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
    }
}

dependencyResolutionManagement {
    // Cho phép cấu hình repository bổ sung (thay vì chặn project-level repo)
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)

    repositories {
        google()
        mavenCentral()
        flatDir {
            dirs("app/libs")
        }
    }
}

rootProject.name = "PRM392_Cinema"
include(":app")
