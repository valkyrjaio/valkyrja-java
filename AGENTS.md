# AGENTS.md

**valkyrja-java — the framework runtime** — Valkyrja **Java** port.

Before doing any work in this repo, read **both** canonical agent guides:

1. **Cross-language** — https://github.com/valkyrjaio/architecture/blob/26.x/AGENTS.md
2. **Java-specific** — https://github.com/valkyrjaio/architecture/blob/26.x/java/AGENTS.md

They define the architecture, naming, testing, 100% coverage, CI, and the
branch/commit/push/PR workflow this repo follows.

## Coverage gate

JaCoCo enforces **100% line coverage and 100% branch coverage**. The `junit`
CI task runs `jacocoTestCoverageVerification`, which fails the build below
that floor. The rule covers each class as well as the whole bundle. One
untested class changes a large bundle ratio very little. The class rule fails
on that class itself.

`.github/ci/junit/build.gradle.kts` holds the exclusions. A threshold below
100% would hide every other gap, so the build keeps the floor and drops the
code instead. JaCoCo excludes a whole class or a whole package, and never a
single branch. Each entry below therefore drops every line and every branch
that the entry names:

- `**/benchmark/**` — performance harnesses rather than production logic.
- `**/application/entry/exchange/**`, `**/application/entry/jetty/**`,
  `**/application/entry/netty/**`, `**/application/entry/tomcat/**` —
  bootstrap glue that starts a live server. The jetty, netty, and tomcat
  adapters then block on the server. The exchange adapters return with the
  server still listening. The `grpc` and `abstract_` entry packages stay
  measured.
- `**/cli/server/support/Exiter.class` — the class guards a real
  `System.exit`. The true arm of that guard stops the test JVM.
- `**/log/logger/abstract_/Logger.class` — the class switches over every
  `LogLevel` constant. The compiler emits a synthetic default that no test
  reaches.

The last two entries drop more than the one branch that each class cannot
cover. A new method on either class meets no coverage requirement. Keep both
classes small, and read a change to them closely.

Add an exclusion only where a test cannot drive the code, or where the code is
not production logic. Give the reason beside the entry. Every other missed
branch is a missing test.
