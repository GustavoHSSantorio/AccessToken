import groovy.util.XmlSlurper
import groovy.util.slurpersupport.GPathResult
import groovy.util.slurpersupport.NodeChild
import groovy.util.slurpersupport.NodeChildren
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import org.gradle.kotlin.dsl.buildscript
import java.io.File
import java.io.FileNotFoundException
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*
import java.util.concurrent.TimeUnit


data class Credential(var versionEnvironment: String, var versionToken: String)

object Credentials{
    val map : HashMap<String, Credential> = hashMapOf()
}

abstract class CredentialsTask : DefaultTask(){

    @get:Input
    abstract val credentialFile : Property<String>

    @TaskAction
    fun getAccessToken() {
        val settings = getMavenSettingsCredentials()

        settings?.forEach {
            (it as? NodeChild)?.run {
                val expiration = getProperty("expiration").toString()
                val id = getProperty("id").toString()

                if(expiration.isNotBlank()) {
                    try {
                        val expirationDate =
                            SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(expiration)
                        val diff = expirationDate.time - Date.from(Instant.now()).time
                        val days = TimeUnit.MILLISECONDS.toDays(diff).toInt() + 1

                        when {
                            days <= 0 -> {
                                logger.error("Error: Your credential with id $id expired in $expiration. Use createCredential task to refresh!")
                            }
                            days == 1 -> {
                                logger.warn("Warning: Your credential with id ${id} expires in $days day ($expiration). You need to refresh this credential today using createCredential task!")
                            }
                            days <= 5 -> {
                                logger.warn("Warning: Your credential with id $id expires in $days days ($expiration)")
                            }
                            else -> {
                                logger.log(
                                    LogLevel.INFO,
                                    "Information: Your credential with id $id expires in $days days (${expiration})"
                                )
                            }
                        }
                    } catch (e: Exception) {
                        logger.error("Error: " + e.message + " - Error parsing date")
                    }
                } else {
                    logger.warn("Warning: Your credential don't have expiration date. Please inform the expiration date with createCredential task!")
                }

                Credentials.map[id] = Credential(
                    getProperty("username").toString(),
                    getProperty("password").toString()
                )
            }
        }
    }

    private fun getMavenSettingsCredentials() : NodeChildren? {
        val userHome = System.getProperty("user.home")
        val mavenSettings = File(userHome, ".m2/${credentialFile.getOrElse("settings.xml")}")

        val xmlSlurper = XmlSlurper()
        return try{
            val output = xmlSlurper.parse(mavenSettings)
            (output.getProperty("servers") as GPathResult).getProperty("server") as NodeChildren
        }catch (e: FileNotFoundException){
            logger.warn("Error: " + e.message + " - Please run createCredentials task in terminal to use plugin")
            null
        }
    }
}

abstract class CreateCredentialTask : DefaultTask(){

    @get:Optional
    @get:Input
    abstract var credentialFile : String?

    @get:Input
    abstract var credentialId : String

    @get:Input
    abstract var credentialUsername : String

    @get:Input
    abstract var credentialPassword : String

    @get:Optional
    @get:Input
    abstract var credentialExpiration : String?

    @Option(option = "file", description = "File name that will be created")
    fun setFile(file : String){
        credentialFile = file
    }

    @Option(option = "id", description = "ID that will be registered in file")
    fun setId(id : String){
        credentialId = id
    }

    @Option(option = "username", description = "Username that will be registered in file")
    fun setUsername(username : String){
        credentialUsername = username
    }

    @Option(option = "password", description = "Password that will be registered in file")
    fun setPassword(password : String){
        credentialPassword = password
    }

    @Option(option = "expiration", description = "Expiration that will be registered in file")
    fun setExpiration(expiration : String){
        credentialExpiration = expiration
    }

    @TaskAction
    fun createCredentialsFile() {
        val userHome = System.getProperty("user.home")
        val mavenSettings = File(userHome, ".m2/${credentialFile ?: "settings.xml" }")

        if(isExpirationValid(credentialExpiration)){
            if(!mavenSettings.exists())
                mavenSettings.delete()

            var textToWrite =
                DEFAULT_FILE
                .replace(ID, credentialId)
                .replace(USERNAME, credentialUsername)
                .replace(TOKEN, credentialPassword)

            if(credentialExpiration != null) {
                textToWrite = textToWrite.replace(EXPIRATION, credentialExpiration!!)
            } else {
                textToWrite = textToWrite.replace(EXPIRATION_LINE, "")
            }

            mavenSettings.createNewFile()
            mavenSettings.writeText(textToWrite)
            logger.log(LogLevel.INFO,"Information: Your credential with id $credentialId was successfully created")
        }
        else{
            logger.error("Error: Failed to parse expiration date and your credential won't be created. Please use the folowing format dd-MM-yyyy.")
        }
    }

    private fun isExpirationValid(expiration: String?) : Boolean =
        expiration?.let {
            try {
                SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(it)
                true
            } catch (e: Exception) {
                false
            }
        } ?: true

    companion object {
        private const val ID = "{ID}"
        private const val USERNAME = "{USERNAME}"
        private const val TOKEN = "{TOKEN}"
        private const val EXPIRATION = "{EXPIRATION}"
        private const val EXPIRATION_LINE = "\t  \t\t<expiration>{EXPIRATION}</expiration>\n"
        private const val DEFAULT_FILE = "<settings xmlns=\"http://maven.apache.org/SETTINGS/1.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "  xsi:schemaLocation=\"http://maven.apache.org/SETTINGS/1.0.0 https://maven.apache.org/xsd/settings-1.0.0.xsd\">\n" +
                "\t <servers>\n" +
                "\t\t<server>\n" +
                "\t  \t\t<id>{ID}</id>\n" +
                "\t  \t\t<username>{USERNAME}</username>\n" +
                "\t  \t\t<password>{TOKEN}</password>\n" +
                EXPIRATION_LINE +
                "\t\t</server>\n" +
                "\t</servers>\n" +
                "</settings>"
    }
}

class CredentialsPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.buildscript {
            target.tasks.register("getCredentials", CredentialsTask::class.java){
                credentialFile.set("settings.xml")
                getAccessToken()
            }

            println(target.tasks.getByPath("getCredentials").path)
        }

        target.tasks.register("createCredentials", CreateCredentialTask::class.java)
    }
}
