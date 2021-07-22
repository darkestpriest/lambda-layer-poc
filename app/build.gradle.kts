plugins {
    kotlin("jvm")
}

dependencies {
    //kotlin
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.2")

    //aws
    implementation("com.amazonaws:aws-lambda-java-core:1.2.1")
    implementation("com.amazonaws:aws-lambda-java-events:3.9.0")

    // logging
    implementation("org.apache.logging.log4j:log4j-api:2.13.3")
    implementation("org.apache.logging.log4j:log4j-core:2.13.3")
    implementation("org.apache.logging.log4j:log4j-slf4j18-impl:2.13.3")
    runtimeOnly("com.amazonaws:aws-lambda-java-log4j2:1.2.0")

    testImplementation(kotlin("test"))
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")

    implementation(project(":repository")) {
        isTransitive = false
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
