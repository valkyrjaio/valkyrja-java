<p align="center"><a href="https://valkyrja.io" target="_blank">
    <img src="https://raw.githubusercontent.com/valkyrjaio/art/refs/heads/master/long-banner/orange/java.png" width="100%">
</a></p>

# Valkyrja

[Valkyrja][Valkyrja url] is a Java framework for web and console applications.

Valkyrja (pronounced "Valk-ear-ya") is the Old Norse spelling for Valkyrie, a
mythical creature that would guide warriors to Valhalla (the afterlife and a
better place) after death. In a similar sense, the Valkyrja framework guides
your application to be in a better state. Fast, light, and robust, Valkyrja
does the heavy lifting so you can focus on your application.

<p>
    <a href="https://central.sonatype.com/artifact/io.valkyrja/valkyrja"><img src="https://img.shields.io/maven-central/v/io.valkyrja/valkyrja?label=Maven%20Central" alt="Latest Stable Version"></a>
    <a href="https://github.com/valkyrjaio/valkyrja-java"><img src="https://img.shields.io/badge/Java-21--25-orange" alt="Java Version"></a>
    <a href="https://github.com/valkyrjaio/valkyrja-java/blob/26.x/LICENSE.md"><img src="https://img.shields.io/badge/license-MIT-blue" alt="License"></a>
    <a href="https://github.com/valkyrjaio/valkyrja-java/actions/workflows/ci.yml?query=branch%3A26.x"><img src="https://github.com/valkyrjaio/valkyrja-java/actions/workflows/ci.yml/badge.svg?branch=26.x" alt="CI Status"></a>
    <a href="https://coveralls.io/github/valkyrjaio/valkyrja-java?branch=26.x"><img src="https://coveralls.io/repos/github/valkyrjaio/valkyrja-java/badge.svg?branch=26.x" alt="Coverage Status"></a>
    <a href="https://sonarcloud.io/summary/new_code?id=valkyrjaio_valkyrja-java"><img src="https://sonarcloud.io/api/project_badges/measure?project=valkyrjaio_valkyrja-java&metric=sqale_rating" alt="Maintainability Rating"></a>
</p>

What's Included
---------------

- **HTTP and CLI kernels** — unified application architecture serving both
  web requests and command-line invocations
- **Dependency injection container** — deferred bindings, contextual
  resolution, and compiled data for fast resolution at runtime
- **Routing** — attribute-based route registration with middleware
  pipelines for both HTTP and CLI
- **Event dispatcher** — decoupled event handling with typed listeners
- **Type system** — primitive wrappers, identifiers, models, and collections
- **Validation** — composable rule contracts and a fluent validator
- **Persistent worker support** — first-class entry points for embedded
  [Tomcat][tomcat url], [Jetty][jetty url], and [Netty][netty url] for
  production-grade performance

Installation
------------

### Start a New Application

The fastest way to start a new Valkyrja application is with the starter
template or the Sindri code generator:

- Use the [`application-java`][starter url] GitHub template ("Use this
  template" button on the repository page)
- Or use [Sindri][sindri url] to scaffold a new project

### Add to an Existing Project

Add the framework as a dependency:

```kotlin
// Gradle (Kotlin DSL)
implementation("io.valkyrja:valkyrja:26.0.0")
```

```xml
<!-- Maven -->
<dependency>
    <groupId>io.valkyrja</groupId>
    <artifactId>valkyrja</artifactId>
    <version>26.0.0</version>
</dependency>
```

Documentation
-------------

Full [documentation][docs url] is baked into the repository so you can
browse it offline. Major areas include:

- [HTTP][http docs url] — routing, controllers, middleware, requests, responses
- [CLI][cli docs url] — commands, input, output, and dispatch
- [Container][container docs url] — dependency injection bindings and resolution
- [Events][events docs url] — event dispatch and listeners

Ecosystem
---------

Valkyrja is the core framework. Surrounding it is an ecosystem of related
projects in the Valkyrjaio organization:

- [**Sindri**][sindri url] — code generator and application creator
- [**Starter (App)**][starter url] — starter template for new applications
- **Worker runtimes** — [Tomcat][tomcat url], [Jetty][jetty url],
  [Netty][netty url]

See the [Valkyrjaio organization page][org url] for the complete listing.

Versioning and Release Process
------------------------------

Valkyrja follows [semantic versioning][semantic versioning url] with a major
release every year, and support for each major version for 2 years from the
date of release.

For more information see our
[Versioning and Release Process documentation][Versioning and Release Process url].

### Supported Versions

Bug fixes are provided until 3 months after the next major release. Security
fixes are provided for 2 years after the initial release.

| Version | Java    | Release        | Bug Fixes Until | Security Fixes Until |
| :------ | :------ | :------------- | :-------------- | :------------------- |
| 26      | 21 – 25 | March 31, 2026 | Q2 2027         | Q1 2028              |
| 27      | 23 – 25 | Q1 2027        | Q2 2028         | Q1 2029              |
| 28      | 25+     | Q1 2028        | Q2 2029         | Q1 2030              |

Contributing
------------

Valkyrja is an open-source, community-driven project. Thank you for your
interest in helping develop, maintain, and release it.

See [`CONTRIBUTING.md`][contributing url] for the submission process and
[`VOCABULARY.md`][vocabulary url] for the terminology used across Valkyrja.

Security Issues
---------------

If you discover a security vulnerability within Valkyrja, please follow our
[disclosure procedure][security vulnerabilities url].

License
-------

Valkyrja is open-source software licensed under the
[MIT license][MIT license url]. See [`LICENSE.md`](./LICENSE.md).

[Valkyrja url]: https://valkyrja.io
[org url]: https://github.com/valkyrjaio
[sindri url]: https://github.com/valkyrjaio/sindri-java
[starter url]: https://github.com/valkyrjaio/application-java
[tomcat url]: https://github.com/valkyrjaio/tomcat
[jetty url]: https://github.com/valkyrjaio/jetty
[netty url]: https://github.com/valkyrjaio/netty
[docs url]: ./src/main/java/io/valkyrja/README.md
[http docs url]: ./src/main/java/io/valkyrja/http
[cli docs url]: ./src/main/java/io/valkyrja/cli
[container docs url]: ./src/main/java/io/valkyrja/container
[events docs url]: ./src/main/java/io/valkyrja/event
[Versioning and Release Process url]: ./src/main/java/io/valkyrja/VERSIONING_AND_RELEASE_PROCESS.md
[contributing url]: https://github.com/valkyrjaio/.github/blob/26.x/CONTRIBUTING.md
[vocabulary url]: https://github.com/valkyrjaio/.github/blob/26.x/VOCABULARY.md
[security vulnerabilities url]: https://github.com/valkyrjaio/.github/blob/26.x/SECURITY.md
[semantic versioning url]: https://semver.org/
[MIT license url]: https://opensource.org/licenses/MIT
[license url]: ./LICENSE.md
