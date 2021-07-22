plugins {
    val kotlinVersion = "1.5.21"
    kotlin("jvm") version kotlinVersion apply false
    kotlin("plugin.serialization") version kotlinVersion apply false
}

subprojects {
    repositories {
        mavenCentral()
    }
}