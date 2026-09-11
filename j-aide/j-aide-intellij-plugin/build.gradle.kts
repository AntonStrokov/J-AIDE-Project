import org.jetbrains.intellij.platform.gradle.TestFrameworkType
import org.w3c.dom.Node
import javax.xml.parsers.DocumentBuilderFactory

plugins {
    id("java")
    id("org.jetbrains.intellij.platform") version "2.16.0"
}

group = "com.antonstrokov.jaide"
version = "0.1.2"

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
    description = "Verifies that backend Maven and IntelliJ plugin versions match."

    doLast {
        val rootPom = file("../pom.xml")

        val document = DocumentBuilderFactory.newInstance()
            .newDocumentBuilder()
            .parse(rootPom)

        val projectElement = document.documentElement

        val backendVersion = (0 until projectElement.childNodes.length)
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

        val pluginVersion = project.version.toString()

        if (backendVersion != pluginVersion) {
            throw GradleException(
                "Release version drift detected: " +
                        "backend=$backendVersion, plugin=$pluginVersion"
            )
        }

        logger.lifecycle(
            "Release versions aligned: $backendVersion"
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
