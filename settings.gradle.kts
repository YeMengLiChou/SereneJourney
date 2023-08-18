pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

// java toolchain
plugins {
    id ("org.gradle.toolchains.foojay-resolver-convention") version ("0.4.0")
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "SereneJourney"
include(":app")
include(":lib_network")
include(":lib_common")
include(":lib_framework")
include(":mod_main")
include(":lib_glide")
include(":lib_room")
include(":mod_login")
include(":mod_user")
include(":mod_settings")
include(":mod_imageShare")
