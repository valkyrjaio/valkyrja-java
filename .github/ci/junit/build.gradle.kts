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
    implementation("com.fasterxml.jackson.core:jackson-databind:2.22.0")
    compileOnly("org.jspecify:jspecify:1.0.0")
    testImplementation("org.junit.jupiter:junit-jupiter:6.1.1")
    testImplementation("org.mockito:mockito-core:5.23.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.23.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
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
    //  - the Sun HttpServer entry adapters (ExchangeHttp/ExchangeCgiHttp) are Java-only bootstrap
    //    glue with no PHP equivalent; their run() starts non-daemon server threads that cannot be
    //    exercised from a unit test without leaking the server / hanging the test JVM.
    classDirectories.setFrom(
            classDirectories.files.map { dir ->
                fileTree(dir) {
                    exclude("**/benchmark/**")
                    exclude("**/application/entry/ExchangeHttp.class")
                    exclude("**/application/entry/ExchangeCgiHttp.class")
                }
            })
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}
