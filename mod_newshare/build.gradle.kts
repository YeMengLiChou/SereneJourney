import com.android.build.api.dsl.ApplicationDefaultConfig

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    if (libs.versions.isModule.get().toBoolean()) {
        alias(libs.plugins.com.android.library)
    } else {
        alias(libs.plugins.com.android.application)
    }
    alias(libs.plugins.org.jetbrains.kotlin.android)
    alias(libs.plugins.com.google.devtools.ksp)
//    id("org.jetbrains.kotlin.kapt")
    id("kotlin-kapt")
}

android {
    namespace = "com.example.mod_newshare"
    resourcePrefix = "newshare_"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        if (!libs.versions.isModule.get().toBoolean() && this is ApplicationDefaultConfig) {
            applicationId = "com.example.newshare"
            versionCode = libs.versions.android.versionCode.get().toInt()
            versionName = libs.versions.android.compileSdk.get()
        }
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    sourceSets {
        this["main"].apply {
            if (libs.versions.isModule.get().toBoolean()) {
                manifest.srcFile("src/main/AndroidManifest.xml")
            } else {
                manifest.srcFile("src/main/debug/AndroidManifest.xml")
            }
        }

    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }


    kotlin {
        jvmToolchain(17)
    }

    buildFeatures {
        viewBinding = true
    }
    
}

dependencies {

    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)

    implementation(libs.theRouter)
    kapt(libs.theRouter.apt)

    implementation(libs.bundles.coroutines)

    implementation(libs.swipeRefreshLayout)
    implementation(libs.lifecycle.runtime)
//    ksp(libs.lifecycle.compiler)

    implementation(libs.bundles.viewModel)
    implementation(project(":lib_framework"))
    implementation(project(":lib_common"))
    implementation(project(":lib_network"))
    implementation(project(":lib_room"))

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
}