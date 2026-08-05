# AGENTS.md

**valkyrja-java — the framework runtime** — Valkyrja **Java** port.

Before doing any work in this repo, read **both** canonical agent guides:

1. **Cross-language** — https://github.com/valkyrjaio/architecture/blob/26.x/AGENTS.md
2. **Java-specific** — https://github.com/valkyrjaio/architecture/blob/26.x/java/AGENTS.md

They define the architecture, naming, testing, 100% coverage, CI, and the
branch/commit/push/PR workflow this repo follows.

## Known coverage baseline

JaCoCo reports **LINE 100%** but **BRANCH ~99.89%** — two long-standing, accepted
missed branches that are **not regressions**. There is no
`jacocoTestCoverageVerification` rule, so `./gradlew ci` still passes:

- `io.valkyrja.log.logger.abstract_.Logger#log` — 1 branch
- `io.valkyrja.cli.server.support.Exiter#exit` — 1 branch (+3 instructions)

If a coverage check surfaces only these two, treat them as the baseline — not
something your change introduced.
