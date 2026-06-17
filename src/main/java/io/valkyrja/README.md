# Documentation

## Prologue

- [Getting Started](GETTING_STARTED.md) — Installation, configuration, and first
  steps
- [Request Lifecycle](LIFECYCLE.md) — What happens from entry point to response,
  for both HTTP and CLI
- [Versioning & Release Process](VERSIONING_AND_RELEASE_PROCESS.md) — Release
  schedule, support policy, and development branches

## Core Concepts

- [The Application](application) — Bootstrap sequence, typed configuration,
  component loading, and the data cache
- [The Container](container) — Dependency injection, service types, service
  providers, and component providers
- [The Dispatcher](dispatch) — Dispatch types, the dispatcher contract, and how
  events, CLI, and HTTP all share the same invocation engine

### HTTP

- [HTTP Routing & Middleware](http) — Route providers, attribute-based
  registration, the middleware pipeline, request and response handling, and the
  HTTP server

### CLI

- [CLI Routing & Commands](cli) — Command providers, attribute-based
  registration, arguments and options, the middleware pipeline, and interactive
  input/output

### Events

- [Event Dispatching](event) — Event dispatcher, listeners, and annotation-based
  registration

## Data & Types

- [Type System](type) — Type contracts, primitive wrappers, enums, models, and
  collections

## Validation

- [Validation](validation) — Rule contracts, built-in rules, and the validator

## Internals

- [Reflection](reflection) — Cached reflection wrapper for classes, methods,
  fields, constructors, and dependency extraction
- [Support](support) — Time/clock freeze utilities and file generator helpers
- [Throwable](throwable) — Exception hierarchy and throwable handler contract

## Services

- [Log](log) — Logging contract with concrete and null implementations
