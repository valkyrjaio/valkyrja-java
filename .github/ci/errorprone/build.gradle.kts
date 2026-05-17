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
    id("com.github.ben-manes.versions") version "0.53.0"
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
    implementation("com.fasterxml.jackson.core:jackson-databind:2.19.0")
    compileOnly("org.jspecify:jspecify:1.0.0")
    errorprone("com.google.errorprone:error_prone_core:2.39.0")
    errorprone("com.uber.nullaway:nullaway:0.12.7")
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
