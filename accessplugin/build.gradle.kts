plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    id("maven")
    id("maven-publish")
    id("com.gradle.plugin-publish") version "0.15.0"
}

repositories {
    jcenter()
}

pluginBundle {
    website = "https://github.com/"
    vcsUrl = "https://github.com/"
    tags = listOf("access token", "artifact token")
}

gradlePlugin {
    plugins {
        create("accessPlugin") {
            id = "br.com.xpinc.accesstoken"
            implementationClass = "AccessTokenPlugin"
            displayName = "Access Token Plugin"
            description = "A plugin to access token from maven local"
        }
    }
}


version = "0.0.0-rc1"