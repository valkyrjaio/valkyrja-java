/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

plugins {
    id("com.diffplug.spotless") version "8.9.0"
    id("com.github.ben-manes.versions") version "0.57.0"
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

spotless {
    java {
        // The JUnit build's tests are the repo's other Java source tree; format them too.
        // Scoped to `src/test/java` on purpose — a `src/test/resources` tree can hold .java files
        // that are test *data* rather than source, parsed as text by the code under test.
        // Formatting them, or injecting a license header, rewrites the very input those tests
        // assert on. sindri-java has exactly such fixtures, and the broader `src/test/**` glob
        // silently corrupted 58 of them there.
        target("src/**/*.java", ".github/ci/junit/src/test/java/**/*.java")
        googleJavaFormat("1.27.0").aosp()
        licenseHeader(
            """/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

"""
        )
    }
}
