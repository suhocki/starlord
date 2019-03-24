import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import org.jetbrains.kotlin.config.KotlinCompilerVersion

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android.extensions")
    kotlin("android")
    kotlin("kapt")
    id("androidx.navigation.safeargs.kotlin")
}

val buildUid = System.getenv("BUILD_COMMIT_SHA") ?: "local"
val isRunningFromTravis = System.getenv("CI") == "true"
val buildVersion = "bash ../versionizer/versionizer.sh name".runCommand()

android {
    compileSdkVersion(28)

    defaultConfig {
        applicationId = "kt.school.starlord"
        minSdkVersion(19)
        targetSdkVersion(28)
        versionCode = "bash ../versionizer/versionizer.sh code".runCommand().toInt()
        versionName = buildVersion
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true

        signingConfigs {
            create("prod") {
                storeFile = file("../keys/keystore.jks")
                storePassword = System.getenv("keystore_store_password")
                keyPassword = System.getenv("keystore_password")
                keyAlias = System.getenv("keystore_alias")
            }
        }

        buildTypes {
            getByName("debug") {
                isMinifyEnabled = false
                versionNameSuffix = "-debug"
            }
            getByName("release") {
                isMinifyEnabled = true
                signingConfig = signingConfigs.getByName("prod")

                proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    file("proguard-rules.pro")
                )
            }
        }

        applicationVariants.all applicationVariants@{
            outputs.all outputs@{
                val buildLabel =
                    if (this@applicationVariants.name == "debug") "_debug"
                    else ""

                (this@outputs as BaseVariantOutputImpl).outputFileName =
                    "starlord_$buildVersion$buildLabel.apk"
            }
        }
    }
}

dependencies {
    val lifecycleVersion = "2.0.0"
    val navigationVersion = "2.1.0-alpha01"
    val koinVersion = "2.0.0-rc-1"
    val ankoVersion = "0.10.8"

    //Core
    implementation(kotlin("stdlib-jdk7", KotlinCompilerVersion.VERSION))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.1.1")
    implementation("androidx.core:core-ktx:1.0.1")
    implementation("androidx.appcompat:appcompat:1.0.2")
    implementation("androidx.constraintlayout:constraintlayout:2.0.0-alpha3")
    //Architecture components
    implementation("androidx.lifecycle:lifecycle-extensions:$lifecycleVersion")
    implementation("androidx.navigation:navigation-fragment-ktx:$navigationVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navigationVersion")
    //Anko
    implementation("org.jetbrains.anko:anko:$ankoVersion")
    implementation("org.jetbrains.anko:anko-sdk25-coroutines:$ankoVersion")
    implementation("org.jetbrains.anko:anko-appcompat-v7-coroutines:$ankoVersion")
    //Koin
    implementation("org.koin:koin-core:$koinVersion")
    implementation("org.koin:koin-androidx-scope:$koinVersion")
    implementation("org.koin:koin-androidx-viewmodel:$koinVersion")
    //Adapter simplify
    implementation("com.hannesdorfmann:adapterdelegates4:4.0.0")

    kapt("androidx.lifecycle:lifecycle-compiler:$lifecycleVersion")
}

gradle.buildFinished {
    println("VersionName: ${android.defaultConfig.versionName}")
    println("VersionCode: ${android.defaultConfig.versionCode}")
    println("BuildUid: $buildUid")
}

fun String.runCommand(workingDir: File = file(".")) =
    ProcessBuilder(*split("\\s".toRegex()).toTypedArray())
        .directory(workingDir)
        .redirectOutput(ProcessBuilder.Redirect.PIPE)
        .redirectError(ProcessBuilder.Redirect.PIPE)
        .start()
        .apply { waitFor(5, TimeUnit.SECONDS) }
        .inputStream
        .bufferedReader()
        .readText()
        .trim()
