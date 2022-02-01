plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
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
            group = "com.github.gustavohssantorio"
            id = "com.github.gustavohssantorio.credentials"
            version = "1.0.0"
            implementationClass = "CredentialsPlugin"
            displayName = "Credentials Access Plugin"
            description = "A plugin get to all credentials in settings.xml, and access it on build.gradle"
        }
    }
}