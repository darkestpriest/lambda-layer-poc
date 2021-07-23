plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    implementation(platform("software.amazon.awssdk:bom:2.17.2"))
    implementation("software.amazon.awssdk:dynamodb") {
        exclude("software.amazon.awssdk", "apache-client")
        exclude("software.amazon.awssdk", "netty-nio-client")
    }
    implementation("software.amazon.awssdk:url-connection-client") {
        because("to avoid to load in runtime unnecessary http clients")
    }

    implementation("org.apache.logging.log4j:log4j-api:2.13.3")
    implementation("org.apache.logging.log4j:log4j-core:2.13.3")
    runtimeOnly("org.apache.logging.log4j:log4j-slf4j18-impl:2.13.3")

    testImplementation(kotlin("test-junit"))
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")
    testImplementation("org.testcontainers:localstack:1.16.0")

    testRuntimeOnly("com.amazonaws:aws-java-sdk-dynamodb:1.11.689") {
        because("to allow test module to instantiate credentials classes")
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.register<Zip>("buildZip") {
    from(tasks.compileKotlin)
    from(tasks.processResources)
    into("java/lib") {
        from(configurations.compileClasspath)
        from(configurations.runtimeClasspath)
    }
}