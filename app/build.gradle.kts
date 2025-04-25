import com.android.build.api.dsl.Packaging

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "ru.example.testapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "ru.example.testapp"
        minSdk = 28
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        ndkVersion = "26.1.10909125"
        ndk {
            abiFilters.addAll(listOf("arm64-v8a", "armeabi-v7a", "armeabi"))
        }
        multiDexEnabled = true
        setProperty("archivesBaseName", "modus-android_v$versionName")
    }
    sourceSets {
        named("main") {
            jniLibs.srcDir("libs")
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        viewBinding = true
        buildConfig = true
    }
    packaging.jniLibs()

    androidResources {
        additionalParameters.add("--no-version-vectors")
    }
}

fun Packaging.jniLibs() {
    jniLibs {
        useLegacyPackaging = true
        pickFirsts.addAll(
            listOf(
                "lib/armeabi-v7a/libc++_shared.so",
                "lib/arm64-v8a/libc++_shared.so",
                "lib/x86/libc++_shared.so",
                "lib/x86_64/libc++_shared.so"
            )
        )
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    implementation(libs.play.services.wearable)
    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.compose.material)
    implementation(libs.compose.foundation)
    implementation(libs.wear.tooling.preview)
    implementation(libs.activity.compose)
    implementation(libs.core.splashscreen)
    implementation(libs.cronet.embedded)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)

    //Desugaring
    coreLibraryDesugaring(libs.desugar.jdk.libs)

    //VLC
    implementation(libs.libvlc.all)

    //Ktor
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    //Multidex
    implementation(libs.multidex)

    //STD
    implementation(libs.kotlin.stdlib)

    //FMPEG
    implementation(libs.ffmpeg.kit.full)


}