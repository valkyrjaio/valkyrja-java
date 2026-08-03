/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

plugins {
    id("io.valkyrja.ci-spotless") version "26.2.0"
    id("com.github.ben-manes.versions") version "0.59.0"
    id("se.patrikerdes.use-latest-versions") version "0.2.19"
}

group = "io.valkyrja"
version = "26.0.0"

repositories {
    mavenCentral()
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

valkyrjaSpotless {
    packageName = "Valkyrja Framework"

    // Each CI build is scoped to `src/test/java`. A `src/test/resources` tree can hold .java
    // files that are test data, and formatting one rewrites the input a test asserts on.
    javaTargets = listOf(
        "src/**/*.java",
        ".github/ci/junit/src/test/java/**/*.java",
        ".github/ci/archunit/src/test/java/**/*.java",
    )
}
