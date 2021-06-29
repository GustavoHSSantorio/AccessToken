import com.android.build.gradle.BaseExtension
import org.jetbrains.kotlin.gradle.internal.AndroidExtensionsExtension
import org.jetbrains.kotlin.gradle.internal.CacheImplementation

buildscript {
    repositories {
        google()
        jcenter()
        mavenCentral()
        maven("https://oss.jfrog.org/artifactory/oss-snapshot-local")
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.72")
        classpath("com.android.tools.build:gradle:3.6.1")
        classpath(kotlin("gradle-plugin", "1.3.72"))
    }
}


allprojects {
    repositories {
        google()
        jcenter()
        mavenCentral()
    }
    configureAndroid()
}

fun Project.configureAndroid() {
    val isAppModule = name == "app"
    val isAccessPlugin = name == "accessplugin"

    when {
        isAppModule -> configureAppAndroid()
        else -> return
    }

    apply(plugin = "kotlin-android")
    apply(plugin = "kotlin-android-extensions")
    apply(plugin = "kotlin-kapt")

    configure<BaseExtension> {

        compileSdkVersion(28)

        defaultConfig {
            minSdkVersion(21)
            targetSdkVersion(28)
            versionCode = 1
            versionName = "0.0.0"
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            vectorDrawables.useSupportLibrary = true
        }

        lintOptions {
            isAbortOnError = false
        }

        lintOptions {
            isCheckReleaseBuilds = false
            isCheckDependencies = true
            isCheckAllWarnings = true
            isWarningsAsErrors = true
            isAbortOnError = false
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
        }

        testOptions {
            unitTests.isIncludeAndroidResources = true
            unitTests.isReturnDefaultValues = true
        }

        sourceSets {
            getByName("main").java.srcDirs("src/main/kotlin")
            getByName("test").java.srcDirs("src/test/kotlin")
            getByName("androidTest").java.srcDirs("src/androidTest/kotlin")
        }

        dexOptions {
            preDexLibraries = true
            maxProcessCount = 8
        }
    }

    configure<AndroidExtensionsExtension> {
        isExperimental = true
        defaultCacheImplementation = CacheImplementation.SPARSE_ARRAY
    }
}

fun Project.configureAppAndroid() {
    apply(plugin = "com.android.application")

    configure<BaseExtension> {
        signingConfigs {
            create("release") {
                isV2SigningEnabled = true
            }
        }

        defaultConfig {
            applicationId = "br.com.xpinc.accesstoken"
        }
    }
}

fun Project.configureAndroidLibrary() {
    apply(plugin = "com.android.library")
}

