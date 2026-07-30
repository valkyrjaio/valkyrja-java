/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

import com.github.spotbugs.snom.Confidence
import com.github.spotbugs.snom.Effort
import com.github.spotbugs.snom.SpotBugsTask

plugins {
    java
    id("com.github.spotbugs") version "6.5.9"
    id("com.github.ben-manes.versions") version "0.57.0"
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
    // The JUnit build's tests are the repo's other Java source tree; analyze them too.
    test {
        java {
            srcDirs("../junit/src/test/java")
        }
    }
}

dependencies {
    // The SpotBugs tool version is declared here rather than via `spotbugs { toolVersion }` so it
    // is a real dependency notation. useLatestVersions only rewrites dependency notations, so a
    // toolVersion string is reported as outdated every run but never updated — it drifts forever.
    spotbugs("com.github.spotbugs:spotbugs:4.10.3")

    implementation("com.fasterxml.jackson.core:jackson-databind:2.22.1")
    compileOnly("org.jspecify:jspecify:1.0.1")
    compileOnly("io.grpc:grpc-api:1.83.1")

    // Compile the optional worker adapters in application.entry.{jetty,netty,tomcat}.
    compileOnly("org.eclipse.jetty:jetty-server:12.1.11")
    compileOnly("org.eclipse.jetty.ee10:jetty-ee10-servlet:12.1.11")
    compileOnly("io.netty:netty-codec-http:4.2.16.Final")
    compileOnly("org.apache.tomcat.embed:tomcat-embed-core:11.0.24")
    compileOnly("io.grpc:grpc-servlet-jakarta:1.83.1")
    compileOnly("io.grpc:grpc-netty-shaded:1.83.1")

    // Mirrors the JUnit build's test classpath — needed only so the tests compile here.
    testImplementation("org.junit.jupiter:junit-jupiter:6.1.2")
    testImplementation("org.mockito:mockito-core:5.23.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.23.0")
    testImplementation("org.jspecify:jspecify:1.0.1")
    testImplementation("io.grpc:grpc-api:1.83.1")
    testImplementation("io.grpc:grpc-netty-shaded:1.83.1")
    testImplementation("io.grpc:grpc-servlet-jakarta:1.83.1")
    testImplementation("org.eclipse.jetty:jetty-server:12.1.11")
    testImplementation("org.eclipse.jetty.ee10:jetty-ee10-servlet:12.1.11")
    testImplementation("io.netty:netty-codec-http:4.2.16.Final")
    testImplementation("org.apache.tomcat.embed:tomcat-embed-core:11.0.24")
}

// Analyzing the tests is the point — running them is the JUnit build's job, so `check` still runs
// spotbugsTest without executing the suite twice.
tasks.test {
    enabled = false
}

spotbugs {
    excludeFilter.set(layout.projectDirectory.file("spotbugs-exclude.xml"))
    effort.set(Effort.MAX)
    reportLevel.set(Confidence.LOW)
}

// The test tree gets its own filter so `src` stays strict — the JUnit/fixture idioms excluded for
// the tests can never loosen the framework's own analysis.
tasks.spotbugsTest {
    excludeFilter.set(layout.projectDirectory.file("spotbugs-exclude-test.xml"))
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

tasks.withType<SpotBugsTask>().configureEach {
    reports.create("html")
}
