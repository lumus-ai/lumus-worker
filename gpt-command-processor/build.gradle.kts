plugins {
    kotlin("jvm") version "1.8.0"
    kotlin("plugin.serialization") version "1.8.0"

    application

    id("com.github.johnrengelman.shadow") version "8.0.0"
    id("java")
}

dependencies {
    implementation(project(":gpt-command-builder", "default"))
    implementation("com.github.ajalt.clikt:clikt:3.5.2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}

application {
    mainClass.set("ai.lumus.MainKt")
}

tasks {
    shadowJar {
        archiveBaseName.set("shadow")
        archiveClassifier.set("")
        archiveVersion.set("")
    }
    build {
        dependsOn(shadowJar)
    }
}