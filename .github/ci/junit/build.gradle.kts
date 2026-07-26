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
    jacoco
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
    implementation("io.grpc:grpc-api:1.69.0")
    testImplementation("org.junit.jupiter:junit-jupiter:6.1.2")
    testImplementation("org.mockito:mockito-core:5.23.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.23.0")
    // Test-only: a real HTTP/2 transport (server + client) for the gRPC end-to-end test. Not a
    // framework dependency — the published artifact keeps io.grpc compileOnly.
    testImplementation("io.grpc:grpc-netty-shaded:1.69.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // Runtime-entry SDKs for the optional worker adapters in application.entry.{jetty,netty,tomcat}.
    // compileOnly so the main src (the adapters) compiles; testImplementation so the smoke tests can
    // boot the real servers. Not framework dependencies — the published artifact keeps them
    // compileOnly (non-transitive).
    compileOnly("org.eclipse.jetty:jetty-server:12.1.11")
    compileOnly("org.eclipse.jetty.ee10:jetty-ee10-servlet:12.1.11")
    compileOnly("io.netty:netty-codec-http:4.2.16.Final")
    compileOnly("org.apache.tomcat.embed:tomcat-embed-core:11.0.24")
    compileOnly("io.grpc:grpc-servlet-jakarta:1.69.0")
    compileOnly("io.grpc:grpc-netty-shaded:1.69.0")
    testImplementation("org.eclipse.jetty:jetty-server:12.1.11")
    testImplementation("org.eclipse.jetty.ee10:jetty-ee10-servlet:12.1.11")
    testImplementation("io.netty:netty-codec-http:4.2.16.Final")
    testImplementation("org.apache.tomcat.embed:tomcat-embed-core:11.0.24")
    testImplementation("io.grpc:grpc-servlet-jakarta:1.69.0")
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
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    // Exclude non-unit-testable infra:
    //  - benchmark harnesses are performance tooling, not production logic.
    //  - the worker-runtime entry adapters (exchange/jetty/netty/tomcat) are bootstrap glue whose
    //    run() starts persistent, non-daemon servers that block forever (server.join() /
    //    awaitTermination() / await()) with JVM shutdown hooks — they cannot be exercised to full
    //    branch coverage from a unit test without leaking the server / hanging the test JVM. The
    //    reusable logic they build on (WorkerHttp / WorkerGrpc / GrpcBridge) is covered directly,
    //    and the adapters are exercised by the smoke tests. Package globs also drop the anonymous
    //    inner classes the HTTP adapters generate (handlers / servlets / channel initializers).
    classDirectories.setFrom(
            classDirectories.files.map { dir ->
                fileTree(dir) {
                    exclude("**/benchmark/**")
                    exclude("**/application/entry/exchange/**")
                    exclude("**/application/entry/jetty/**")
                    exclude("**/application/entry/netty/**")
                    exclude("**/application/entry/tomcat/**")
                }
            })
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}
