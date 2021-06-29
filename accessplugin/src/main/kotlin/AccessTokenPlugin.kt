import groovy.util.XmlSlurper
import groovy.util.slurpersupport.GPathResult
import groovy.util.slurpersupport.NodeChild
import groovy.util.slurpersupport.NodeChildren
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import java.io.File

object AccessToken{
    var versionEnviroment = ""
    var versionToken = ""
}

abstract class AccessTokenTask : DefaultTask(){

    @TaskAction
    fun getAccessToken() {
        val settings = getMavenSettingsCredentials()

        println("Resulttttttt -> ${settings}")

        settings.forEach {
            println("meuuuuuu -> ${it!!::class.java}")
            (it as? NodeChild)?.run {
                if(getProperty("id") == "Android-Repositories") {
                    AccessToken.versionEnviroment = getProperty("username").toString()
                    AccessToken.versionToken = getProperty("password").toString()
                    println("AMAMAMAMA -> ${AccessToken.versionEnviroment} ${AccessToken.versionToken}")
                }
            }
        }
    }

    private fun getMavenSettingsCredentials() : NodeChildren {
        val userHome = System.getProperty("user.home")
        val mavenSettings = File(userHome, ".m2/settings.xml")
        val xmlSlurper = XmlSlurper()
        val output = xmlSlurper.parse(mavenSettings)
        return (output.getProperty("servers") as GPathResult).getProperty("server") as NodeChildren
    }
}

class AccessTokenPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.afterEvaluate {
            println("AAAAAAAAA")
            tasks.register("generateAccessToken", AccessTokenTask::class.java){
                println("BBBBBBBB")
                getAccessToken()
            }

            println(tasks.getByPath("generateAccessToken").path)
        }
    }
}