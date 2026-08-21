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

### HTTP

- [HTTP Routing & Middleware](http) — Route providers, attribute-based
  registration, the middleware pipeline, request and response handling, and the
  HTTP server

### CLI

- [CLI Routing & Commands](cli) — Command providers, attribute-based
  registration, arguments and options, the middleware pipeline, and interactive
  input/output

### gRPC

- [gRPC Services](grpc) — Service providers, attribute-based registration, the
  middleware pipeline, the buffered and streaming models, and cancellation

### Events

- [Event Dispatching](event) — Event dispatcher, listeners, and the listener
  collection

## Data & Types

- [Type System](type) — The route parameter cast, and the arrayable contract

## Validation

- [Validation](validation) — The rule contract and the validator contract

## Internals

- [Reflection](reflection) — Cached reflection wrapper for instances,
  constructors, and methods
- [Support](support) — Time/clock freeze utilities and file generator helpers
- [Throwable](throwable) — Exception hierarchy and throwable handler contract

## Services

- [Log](log) — Logging contract with file and null implementations
