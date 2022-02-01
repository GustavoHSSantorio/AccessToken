import com.android.build.gradle.BaseExtension

buildscript {
    repositories {
        mavenCentral()
        mavenLocal()
        google()
        maven("https://oss.jfrog.org/artifactory/oss-snapshot-local")
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.31")
        classpath("com.android.tools.build:gradle:7.0.4")
        classpath(kotlin("gradle-plugin", "1.5.31"))
    }
}


allprojects {
    repositories {
        google()
        mavenCentral()
    }
    configureAndroid()
}

fun Project.configureAndroid() {
    val isAppModule = name == "app"

    when {
        isAppModule -> configureAppAndroid()
        else -> return
    }

    apply(plugin = "kotlin-android")
    apply(plugin = "kotlin-kapt")

    configure<BaseExtension> {

        compileSdkVersion(28)

        defaultConfig {
            minSdk = 21
            targetSdk = 29
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
    }
}

fun Project.configureAppAndroid() {
    apply(plugin = "com.android.application")

    configure<BaseExtension> {

        defaultConfig {
            applicationId = "com.gustavohssantorio.accesstoken"
        }
    }
}

fun Project.configureAndroidLibrary() {
    apply(plugin = "com.android.library")
}