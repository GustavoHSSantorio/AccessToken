plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    id("maven")
    id("maven-publish")
    id("com.gradle.plugin-publish") version "0.15.0"
}

pluginBundle {
    website = "https://github.com/GustavoHSSantorio"
    vcsUrl = "https://github.com/GustavoHSSantorio/AccessToken"
    tags = listOf("credentials", "settings", "access", "authentication")
}

gradlePlugin {
    plugins {
        create("credentials") {
            group = "org.github.gustavohssantorio"
            id = "org.github.gustavohssantorio.credentials"
            version = "0.0.1"
            implementationClass = "AccessTokenPlugin"
            displayName = "Credentials Access Plugin"
            description = "A plugin get to all credentials in settings.xml, and access it on build.gradle"
        }
    }
}