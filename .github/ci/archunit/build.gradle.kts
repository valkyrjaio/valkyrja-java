/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

plugins {
    java
    id("com.github.ben-manes.versions") version "0.61.0"
    id("se.patrikerdes.use-latest-versions") version "0.2.19"
}

group = "io.valkyrja"
version = "26.0.0"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
}

sourceSets {
    main {
        java {
            srcDirs("../../../src/main/java")
        }
    }
    // The JUnit build's tests, compiled but never executed — ArchUnit reads the resulting
    // bytecode. Running the suite is the JUnit build's job. Kept out of the `test` source set so
    // ArchitectureTest's classpath scan keeps seeing `src` only: the test tree has its own
    // taxonomy and is checked by TestArchitectureTest, which imports it by path.
    create("testTree") {
        java {
            srcDirs("../junit/src/test/java")
        }
        compileClasspath += sourceSets["main"].output
    }
}

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-databind:2.22.2")
    compileOnly("org.jspecify:jspecify:1.0.1")
    compileOnly("io.grpc:grpc-api:1.84.0")
    testImplementation("com.tngtech.archunit:archunit-junit5:1.5.0")
    testImplementation("org.junit.jupiter:junit-jupiter:6.1.3")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // Compile the optional worker adapters in application.entry.{jetty,netty,tomcat}.
    compileOnly("org.eclipse.jetty:jetty-server:12.1.12")
    compileOnly("org.eclipse.jetty.ee10:jetty-ee10-servlet:12.1.12")
    compileOnly("io.netty:netty-codec-http:4.2.17.Final")
    compileOnly("org.apache.tomcat.embed:tomcat-embed-core:11.0.25")
    compileOnly("io.grpc:grpc-servlet-jakarta:1.84.0")
    compileOnly("io.grpc:grpc-netty-shaded:1.84.0")

    // Mirrors the JUnit build's test classpath — needed only so the tests compile here.
    "testTreeImplementation"("com.fasterxml.jackson.core:jackson-databind:2.22.2")
    "testTreeImplementation"("org.junit.jupiter:junit-jupiter:6.1.3")
    "testTreeImplementation"("org.mockito:mockito-core:5.23.0")
    "testTreeImplementation"("org.mockito:mockito-junit-jupiter:5.23.0")
    "testTreeImplementation"("org.jspecify:jspecify:1.0.1")
    "testTreeImplementation"("io.grpc:grpc-api:1.84.0")
    "testTreeImplementation"("io.grpc:grpc-netty-shaded:1.84.0")
    "testTreeImplementation"("io.grpc:grpc-servlet-jakarta:1.84.0")
    "testTreeImplementation"("org.eclipse.jetty:jetty-server:12.1.12")
    "testTreeImplementation"("org.eclipse.jetty.ee10:jetty-ee10-servlet:12.1.12")
    "testTreeImplementation"("io.netty:netty-codec-http:4.2.17.Final")
    "testTreeImplementation"("org.apache.tomcat.embed:tomcat-embed-core:11.0.25")
}

fun isNonStable(version: String): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.uppercase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(version)
    return isStable.not()
}

tasks.named<com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask>("dependencyUpdates") {
    rejectVersionIf { isNonStable(candidate.version) }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.test {
    useJUnitPlatform()
    dependsOn("testTreeClasses")

    val testTreeClasses = sourceSets["testTree"].output.classesDirs

    // The test tree is read by path, not off the test classpath, so Gradle cannot infer that it
    // affects this task. Without declaring it, `test` stays UP-TO-DATE when only the tests change
    // and TestArchitectureTest silently passes against stale bytecode — the exact blind spot this
    // build is meant to close.
    inputs.files(testTreeClasses)
            .withPropertyName("testTreeClasses")
            .withPathSensitivity(PathSensitivity.RELATIVE)

    // Where TestArchitectureTest imports the compiled test tree from.
    systemProperty("valkyrja.testTreeClasses", testTreeClasses.asPath)
}
