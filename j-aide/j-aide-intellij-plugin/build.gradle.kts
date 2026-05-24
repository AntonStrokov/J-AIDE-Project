plugins {
    id("java")
    id("org.jetbrains.intellij.platform") version "2.16.0"
}

group = "com.antonstrokov.jaide"
version = "0.1.0"

repositories {
    mavenCentral()

    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-databind:2.20.1")

    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")

    intellijPlatform {
        intellijIdeaCommunity("2025.1")
    }
}

tasks.test {
    useJUnitPlatform()
}
