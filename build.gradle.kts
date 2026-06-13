import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import org.w3c.dom.Element
import org.w3c.dom.Node

plugins {
    java
    `maven-publish`
    jacoco
}

group = "site.maien"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.antlr:antlr4:4.13.2")
    implementation("org.slf4j:slf4j-simple:2.0.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.7.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
    jvmArgs("--add-opens", "java.base/java.lang=ALL-UNNAMED")
    finalizedBy(tasks.jacocoTestReport) // Automatically generate coverage report after running tests
}

tasks.jacocoTestReport {
    dependsOn(tasks.test) // Ensure tests have run before generating report
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

// Parse credentials from local ~/.m2/settings.xml
fun getMavenCredentials(serverId: String): Pair<String, String>? {
    val settingsFile = File(System.getProperty("user.home"), ".m2/settings.xml")
    if (!settingsFile.exists()) return null
    try {
        val dbFactory = DocumentBuilderFactory.newInstance()
        val dBuilder = dbFactory.newDocumentBuilder()
        val doc = dBuilder.parse(settingsFile)
        doc.documentElement.normalize()
        val servers = doc.getElementsByTagName("server")
        for (i in 0 until servers.length) {
            val server = servers.item(i)
            if (server.nodeType == Node.ELEMENT_NODE) {
                val element = server as Element
                val id = element.getElementsByTagName("id").item(0)?.textContent
                if (id == serverId) {
                    val username = element.getElementsByTagName("username").item(0)?.textContent ?: ""
                    val password = element.getElementsByTagName("password").item(0)?.textContent ?: ""
                    return Pair(username, password)
                }
            }
        }
    } catch (e: Exception) {
        // ignore or log
    }
    return null
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
    repositories {
        maven {
            val releasesRepoUrl = uri("https://packages.aliyun.com/6125a66357e7cd986dfac90b/maven/2131514-release-kucx67")
            val snapshotsRepoUrl = uri("https://packages.aliyun.com/6125a66357e7cd986dfac90b/maven/2131514-snapshot-xjndpc")
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
            
            val serverId = if (version.toString().endsWith("SNAPSHOT")) "2131514-snapshot-XjNDpc" else "2131514-release-kUcX67"
            val localCreds = getMavenCredentials(serverId)
            credentials {
                username = System.getenv("ALIYUN_USERNAME") ?: localCreds?.first ?: ""
                password = System.getenv("ALIYUN_PASSWORD") ?: localCreds?.second ?: ""
            }
        }
    }
}
