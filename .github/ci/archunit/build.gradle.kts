/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

plugins {
    java
    id("com.github.ben-manes.versions") version "0.54.0"
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
}

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-databind:2.22.1")
    compileOnly("org.jspecify:jspecify:1.0.0")
    compileOnly("io.grpc:grpc-api:1.69.0")
    testImplementation("com.tngtech.archunit:archunit-junit5:1.4.2")
    testImplementation("org.junit.jupiter:junit-jupiter:6.1.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // Compile the optional worker adapters in application.entry.{jetty,netty,tomcat}.
    compileOnly("org.eclipse.jetty:jetty-server:12.1.11")
    compileOnly("org.eclipse.jetty.ee10:jetty-ee10-servlet:12.1.11")
    compileOnly("io.netty:netty-codec-http:4.2.16.Final")
    compileOnly("org.apache.tomcat.embed:tomcat-embed-core:11.0.24")
    compileOnly("io.grpc:grpc-servlet-jakarta:1.69.0")
    compileOnly("io.grpc:grpc-netty-shaded:1.69.0")
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
}
