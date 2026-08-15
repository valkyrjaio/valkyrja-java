# AGENTS.md

**valkyrja-java — the framework runtime** — Valkyrja **Java** port.

Before doing any work in this repo, read **both** canonical agent guides:

1. **Cross-language** — https://github.com/valkyrjaio/architecture/blob/26.x/AGENTS.md
2. **Java-specific** — https://github.com/valkyrjaio/architecture/blob/26.x/java/AGENTS.md

They define the architecture, naming, testing, 100% coverage, CI, and the
branch/commit/push/PR workflow this repo follows.

## Coverage gate

JaCoCo enforces **100% line coverage and 100% branch coverage**. The `junit` CI task
runs `jacocoTestCoverageVerification`, which fails the build below that floor. The
rule applies to each class as well as to the whole bundle. A large, well-covered
codebase absorbs one untested new class almost without moving, so the bundle rule
alone would not catch it.

Code that a unit test cannot reach is **excluded** in
`.github/ci/junit/build.gradle.kts`. It is not accommodated by a threshold below
100%, because a lower floor stops the gate from catching everything else. The
exclusions are the benchmark harnesses, the worker-runtime entry adapters, and two
single branches:

- `io.valkyrja.log.logger.abstract_.Logger#log` — the synthetic default that javac
  emits for a switch over every enum constant.
- `io.valkyrja.cli.server.support.Exiter#exit` — the guard over a real
  `System.exit`, whose true arm stops the test JVM.

Exclude a class only if a test cannot reach it. Every other missed branch is a
missing test.
