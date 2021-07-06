# Credentials Plugin
  
## Description

This plugin's goal is to access settings.xml file of your local maven (/.m2) in build.gradle, this way you can put your credentials in a single file, and access in your projects.

## Import

In dependencies block
```
    classpath("org.github.gustavohssantorio:credentials:{plugin version}")
```

In repositories block
```
    maven("https://plugins.gradle.org/m2/")
```

To apply the plugin
```
    apply(plugin = "org.github.gustavohssantorio.credentials")
```

## Usage

To use in your build.gradle files
```
    maven("repository url") {
            name = "Feed name"
            val androidCredentials = Credentials.map[name]           
            authentication {
                create<BasicAuthentication>("basic")
            }
            credentials {
                username = androidCredentials?.versionEnvironment 
                password = androidCredentials?.versionToken
            }
        }
```