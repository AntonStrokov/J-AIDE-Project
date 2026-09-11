import org.jetbrains.intellij.platform.gradle.TestFrameworkType
import org.w3c.dom.Node
import javax.xml.parsers.DocumentBuilderFactory

fun readRootMavenVersion(): String {
    val rootPom = file("../pom.xml")

    val document = DocumentBuilderFactory.newInstance()
        .newDocumentBuilder()
        .parse(rootPom)

    val projectElement = document.documentElement

    return (0 until projectElement.childNodes.length)
        .asSequence()
        .map { projectElement.childNodes.item(it) }
        .firstOrNull {
            it.nodeType == Node.ELEMENT_NODE &&
                    it.nodeName == "version"
        }
        ?.textContent
        ?.trim()
        ?: throw GradleException(
            "Root Maven project version not found in ${rootPom.path}"
        )
}

plugins {
    id("java")
    id("org.jetbrains.intellij.platform") version "2.16.0"
}

group = "com.antonstrokov.jaide"
version = readRootMavenVersion()

repositories {
    mavenCentral()

    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-databind:2.20.1")

    testImplementation("org.junit.jupiter:junit-jupiter:5.14.4")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testRuntimeOnly("junit:junit:4.13.2")

    intellijPlatform {
        intellijIdeaCommunity("2025.1")
        bundledPlugin("com.intellij.java")
        testBundledPlugin("org.jetbrains.kotlin")
        testFramework(TestFrameworkType.Platform)
        testFramework(TestFrameworkType.Plugin.Java)
    }
}

val verifyReleaseVersionAlignment by tasks.registering {
    group = "verification"
    description = "Verifies that packaged plugin metadata matches the Maven release version."

    dependsOn("patchPluginXml")

    doLast {
        val expectedVersion = readRootMavenVersion()

        val patchedPluginXml = layout.buildDirectory
            .file("tmp/patchPluginXml/plugin.xml")
            .get()
            .asFile

        val document = DocumentBuilderFactory.newInstance()
            .newDocumentBuilder()
            .parse(patchedPluginXml)

        val pluginElement = document.documentElement

        val packagedPluginVersion = (0 until pluginElement.childNodes.length)
            .asSequence()
            .map { pluginElement.childNodes.item(it) }
            .firstOrNull {
                it.nodeType == Node.ELEMENT_NODE &&
                        it.nodeName == "version"
            }
            ?.textContent
            ?.trim()
            ?: throw GradleException(
                "Plugin version not found in ${patchedPluginXml.path}"
            )

        if (expectedVersion != packagedPluginVersion) {
            throw GradleException(
                "Release version metadata mismatch: " +
                        "maven=$expectedVersion, packagedPlugin=$packagedPluginVersion"
            )
        }

        logger.lifecycle(
            "Release version metadata verified: $expectedVersion"
        )
    }
}

tasks.test {
    dependsOn(verifyReleaseVersionAlignment)
    useJUnitPlatform()
}

tasks.named("buildPlugin") {
    dependsOn(verifyReleaseVersionAlignment)
}
