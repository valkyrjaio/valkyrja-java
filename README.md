<p align="center"><a href="https://valkyrja.io" target="_blank">
    <img src="https://raw.githubusercontent.com/valkyrjaio/art/refs/heads/master/full-logo/orange/java.png" width="400">
</a></p>

# Valkyrja

[Valkyrja][Valkyrja url] is a Java framework for web and console applications.

About Valkyrja
--------------

> This repository contains the core code of the Valkyrja framework.

Valkyrja (pronounced "Valk-ear-ya") is the Old Norse spelling for Valkyrie, a
mythical creature that would guide warriors to Valhalla (the afterlife and a
better place) after death. In a similar sense, the Valkyrja framework guides
your application to be in a better state. Let this fast, light, and robust
framework do the heavy lifting for your app.

<p>
    <a href="https://packagist.org/packages/valkyrja/valkyrja"><img src="https://poser.pugx.org/valkyrja/valkyrja-java/require/java" alt="Java Version Require"></a>
    <a href="https://packagist.org/packages/valkyrja/valkyrja"><img src="https://poser.pugx.org/valkyrja/valkyrja-java/v" alt="Latest Stable Version"></a>
    <a href="https://packagist.org/packages/valkyrja/valkyrja"><img src="https://poser.pugx.org/valkyrja/valkyrja-java/license" alt="License"></a>
    <!-- <a href="https://packagist.org/packages/valkyrja/valkyrja"><img src="https://poser.pugx.org/valkyrja/valkyrja-java/downloads" alt="Total Downloads"></a>-->
    <a href="https://scrutinizer-ci.com/g/valkyrjaio/valkyrja/?branch=master"><img src="https://scrutinizer-ci.com/g/valkyrjaio/valkyrja-java/badges/quality-score.png?b=master" alt="Scrutinizer"></a>
    <a href="https://coveralls.io/github/valkyrjaio/valkyrja?branch=master"><img src="https://coveralls.io/repos/github/valkyrjaio/valkyrja-java/badge.svg?branch=master" alt="Coverage Status" /></a>
    <a href="https://shepherd.dev/github/valkyrjaio/valkyrja"><img src="https://shepherd.dev/github/valkyrjaio/valkyrja-java/coverage.svg" alt="Psalm Shepherd" /></a>
    <a href="https://sonarcloud.io/summary/new_code?id=valkyrjaio_valkyrja-java"><img src="https://sonarcloud.io/api/project_badges/measure?project=valkyrjaio_valkyrja-java&metric=sqale_rating" alt="Maintainability Rating" /></a>
</p>

Build Status
------------

<table>
    <tbody>
        <tr>
            <td>Linting</td>
            <td>
                <a href="https://github.com/valkyrjaio/valkyrja-java/actions/workflows/spotless.yml?query=branch%3Amaster"><img src="https://github.com/valkyrjaio/valkyrja-java/actions/workflows/spotless.yml/badge.svg?branch=master" alt="PHP Code Sniffer Build Status"></a>
            </td>
        </tr>
        <tr>
            <td>Coding Rules</td>
            <td>
                <a href="https://github.com/valkyrjaio/valkyrja-java/actions/workflows/phparkitect.yml?query=branch%3Amaster"><img src="https://github.com/valkyrjaio/valkyrja/actions/workflows/phparkitect.yml/badge.svg?branch=master" alt="PHPArkitect Build Status"></a>
            </td>
            <td>
                <a href="https://github.com/valkyrjaio/valkyrja-java/actions/workflows/rector.yml?query=branch%3Amaster"><img src="https://github.com/valkyrjaio/valkyrja/actions/workflows/rector.yml/badge.svg?branch=master" alt="Rector Build Status"></a>
            </td>
        </tr>
        <tr>
            <td>Static Analysis</td>
            <td>
                <a href="https://github.com/valkyrjaio/valkyrja-java/actions/workflows/phpstan.yml?query=branch%3Amaster"><img src="https://github.com/valkyrjaio/valkyrja/actions/workflows/phpstan.yml/badge.svg?branch=master" alt="PHPStan Build Status"></a>
            </td>
            <td>
                <a href="https://github.com/valkyrjaio/valkyrja-java/actions/workflows/psalm.yml?query=branch%3Amaster"><img src="https://github.com/valkyrjaio/valkyrja/actions/workflows/psalm.yml/badge.svg?branch=master" alt="Psalm Build Status"></a>
            </td>
        </tr>
        <tr>
            <td>Testing</td>
            <td>
                <a href="https://github.com/valkyrjaio/valkyrja-java/actions/workflows/phpunit.yml?query=branch%3Amaster"><img src="https://github.com/valkyrjaio/valkyrja/actions/workflows/phpunit.yml/badge.svg?branch=master" alt="PHPUnit Build Status"></a>
            </td>
            <td>
                <a href="https://github.com/valkyrjaio/valkyrja-java/actions/workflows/validate-composer.yml?query=branch%3Amaster"><img src="https://github.com/valkyrjaio/valkyrja/actions/workflows/validate-composer.yml/badge.svg?branch=master" alt="Validate Composer Build Status"></a>
            </td>
        </tr>
    </tbody>
</table>

Documentation
-------------

The Valkyrja [documentation][docs url] is baked into the repo so you can
access it even when working offline.

Installation
------------

There are two ways to install the Valkyrja framework.

### Application Skeleton

Clone the [Valkyrja Application][Valkyrja Application url] and start from there.

Versioning and Release Process
------------------------------

Valkyrja uses [semantic versioning][semantic versioning url] with a major
release every year, and support for each major version for 2 years from the
date of release.

For more information view our
[Versioning and Release Process documentation][Versioning and Release Process url].

### Supported Versions

Bug fixes will be provided until 3 months after the next major release. Security
fixes will be provided for 2 years after the initial release.

| Version | PHP (*)   | Release | Bug Fixes Until | Security Fixes Until |
|:--------|:----------|:--------|:----------------|:---------------------|
| 26      | 8.4 - 8.6 | Q2 2026 | Q2 2027         | Q1 2028              |
| 27      | 8.5 - 8.6 | Q1 2027 | Q2 2028         | Q1 2029              |
| 28      | 8.6+      | Q1 2028 | Q2 2029         | Q1 2030              |

(*) Supported PHP versions

Contributing
------------

Valkyrja is an Open Source, community-driven project.

Thank you for your interest in helping us develop, maintain, and release the
Valkyrja framework!

You can find more information in our
[Contributing documentation][contributing url].

Security Issues
---------------

If you discover a security vulnerability within Valkyrja, please follow our
[disclosure procedure][security vulnerabilities url].

License
-------

The Valkyrja framework is open-sourced software licensed under
the [MIT license][MIT license url]. You can view the
[Valkyrja License here][license url].

[Valkyrja url]: https://valkyrja.io

[github main]: https://github.com/valkyrjaio

[Valkyrja Application url]: https://github.com/valkyrjaio/application-java

[docs url]: ./src/main/java/io/valkyrja/README.md

[New Project Guide url]: src/main/java/io/valkyrja/GETTING_STARTED.md

[Versioning and Release Process url]: ./src/main/java/io/valkyrja/VERSIONING_AND_RELEASE_PROCESS.md

[security vulnerabilities url]: ./SECURITY.md

[semantic versioning url]: https://semver.org/

[MIT license url]: https://opensource.org/licenses/MIT

[license url]: ./LICENSE.md

[contributing url]: ./CONTRIBUTING.md
