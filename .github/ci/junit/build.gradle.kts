/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

plugins {
    java
    jacoco
    id("com.github.ben-manes.versions") version "0.59.0"
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
    // The JaCoCo tool version is declared here rather than left to the plugin default (or set via
    // `jacoco { toolVersion }`) so it is a real dependency notation. useLatestVersions only
    // rewrites dependency notations, so an implicit or toolVersion-pinned tool is reported as
    // outdated every run but never updated — it drifts forever.
    jacocoAgent("org.jacoco:org.jacoco.agent:0.8.15")
    jacocoAnt("org.jacoco:org.jacoco.ant:0.8.15")

    implementation("com.fasterxml.jackson.core:jackson-databind:2.22.1")
    compileOnly("org.jspecify:jspecify:1.0.1")
    implementation("io.grpc:grpc-api:1.83.1")
    testImplementation("org.junit.jupiter:junit-jupiter:6.1.2")
    testImplementation("org.mockito:mockito-core:5.23.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.23.0")
    // Test-only: a real HTTP/2 transport (server + client) for the gRPC end-to-end test. Not a
    // framework dependency — the published artifact keeps io.grpc compileOnly.
    testImplementation("io.grpc:grpc-netty-shaded:1.83.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // Runtime-entry SDKs for the optional worker adapters in application.entry.{jetty,netty,tomcat}.
    // compileOnly so the main src (the adapters) compiles; testImplementation so the smoke tests can
    // boot the real servers. Not framework dependencies — the published artifact keeps them
    // compileOnly (non-transitive).
    compileOnly("org.eclipse.jetty:jetty-server:12.1.12")
    compileOnly("org.eclipse.jetty.ee10:jetty-ee10-servlet:12.1.12")
    compileOnly("io.netty:netty-codec-http:4.2.17.Final")
    compileOnly("org.apache.tomcat.embed:tomcat-embed-core:11.0.24")
    compileOnly("io.grpc:grpc-servlet-jakarta:1.83.1")
    compileOnly("io.grpc:grpc-netty-shaded:1.83.1")
    testImplementation("org.eclipse.jetty:jetty-server:12.1.12")
    testImplementation("org.eclipse.jetty.ee10:jetty-ee10-servlet:12.1.12")
    testImplementation("io.netty:netty-codec-http:4.2.17.Final")
    testImplementation("org.apache.tomcat.embed:tomcat-embed-core:11.0.24")
    testImplementation("io.grpc:grpc-servlet-jakarta:1.83.1")
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

// Exclude non-unit-testable infra:
//  - benchmark harnesses are performance tooling, not production logic.
//  - the worker-runtime entry adapters (exchange/jetty/netty/tomcat) are bootstrap glue whose
//    run() starts persistent, non-daemon servers that block forever (server.join() /
//    awaitTermination() / await()) with JVM shutdown hooks — they cannot be exercised to full
//    branch coverage from a unit test without leaking the server / hanging the test JVM. The
//    reusable logic they build on (WorkerHttp / WorkerGrpc / GrpcBridge) is covered directly,
//    and the adapters are exercised by the smoke tests. Package globs also drop the anonymous
//    inner classes the HTTP adapters generate (handlers / servlets / channel initializers).
//  - Exiter and Logger each carry exactly one branch no test can take. Exiter#exit guards a real
//    System.exit: taking the true arm kills the test JVM, which is why freeze()/frozenCallback()
//    exists as a seam at all — the seam's own guard is the irreducible remainder. Logger#log
//    switches over every LogLevel constant, and javac still emits a synthetic default for an
//    exhaustive enum switch; it is reachable only if the enum gains a constant after this class is
//    compiled. Both are excluded here rather than accommodated by a sub-100 threshold, which would
//    stop the floor from catching anything else.
// Shared by jacocoTestReport and jacocoTestCoverageVerification so the gate measures exactly what
// the report shows — scoping only one of them silently lets the other disagree.
val coverageExclusions = listOf(
        "**/benchmark/**",
        "**/application/entry/exchange/**",
        "**/application/entry/jetty/**",
        "**/application/entry/netty/**",
        "**/application/entry/tomcat/**",
        "**/cli/server/support/Exiter.class",
        "**/log/logger/abstract_/Logger.class",
)

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    classDirectories.setFrom(
            classDirectories.files.map { dir ->
                fileTree(dir) { exclude(coverageExclusions) }
            })
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

// The floor. Coverage was reported here and enforced nowhere: `junit` ran `test` finalized by
// `jacocoTestReport`, so the report was generated and then nothing asserted anything about it.
tasks.jacocoTestCoverageVerification {
    dependsOn(tasks.test)
    classDirectories.setFrom(
            classDirectories.files.map { dir ->
                fileTree(dir) { exclude(coverageExclusions) }
            })
    violationRules {
        rule {
            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = "1.00".toBigDecimal()
            }
            limit {
                counter = "BRANCH"
                value = "COVEREDRATIO"
                minimum = "1.00".toBigDecimal()
            }
        }
        // Per class as well as per bundle. A bundle-wide rule is not enough: a large, well-covered
        // codebase absorbs one entirely untested new class almost without moving, so the aggregate
        // stays high while the new file is at zero. A class-level rule fails on that file itself.
        rule {
            element = "CLASS"
            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = "1.00".toBigDecimal()
            }
            limit {
                counter = "BRANCH"
                value = "COVEREDRATIO"
                minimum = "1.00".toBigDecimal()
            }
        }
    }
}
