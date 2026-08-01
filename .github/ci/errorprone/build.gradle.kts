/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

import net.ltgt.gradle.errorprone.CheckSeverity
import net.ltgt.gradle.errorprone.errorprone

plugins {
    java
    id("net.ltgt.errorprone") version "5.1.0"
    id("com.github.ben-manes.versions") version "0.58.0"
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
    implementation("com.fasterxml.jackson.core:jackson-databind:2.22.1")
    compileOnly("org.jspecify:jspecify:1.0.1")
    compileOnly("io.grpc:grpc-api:1.83.1")
    errorprone("com.google.errorprone:error_prone_core:2.50.0")
    errorprone("com.uber.nullaway:nullaway:0.13.8")

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

// Compiling the tests is the point — running them is the JUnit build's job, so `build` compiles
// the test sources (Error Prone runs as part of that) without executing the suite twice.
tasks.test {
    enabled = false
}

tasks.named("check") {
    dependsOn(tasks.compileTestJava)
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

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.errorprone {
        check("NullAway", CheckSeverity.ERROR)
        option("NullAway:AnnotatedPackages", "io.valkyrja")
    }
}

// NullAway enforces a nullness contract on the framework's own API. The tests deliberately break
// that contract — `new Stream().write(null)`, `StreamFactory.isModeWriteable(null)`,
// `new Metadata().with("k", null)` — because that is the only way to reach the defensive guards
// those methods exist to provide, and the canonical guide requires synthetic inputs to cover
// guards normal input cannot reach. Enforcing NullAway here would mean deleting the tests that
// hold branch coverage at 100%, so it is scoped to `src`; every other Error Prone check still
// applies to the test tree.
tasks.compileTestJava {
    options.errorprone {
        check("NullAway", CheckSeverity.OFF)
    }
}
