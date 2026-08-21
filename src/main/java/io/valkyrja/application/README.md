# The Application

## Introduction

The application component holds the entry points, the configuration, the
component provider tree, and the directory helper. It starts the framework, and
it holds the container that every other component resolves from.

This document covers each part.

- [Entry points](#entry-points) — the class that starts one protocol.
- [Configuration](#configuration) — the config contracts and their defaults.
- [The bootstrap sequence](#the-bootstrap-sequence) — what `App.start` does.
- [The provider hierarchy](#the-provider-hierarchy) — how a component loads.
- [Persistent workers](#persistent-workers) — the boot-once runtimes.
- [Directories](#directories) — the path helper.
- [Framework information](#framework-information) — the version constants.

## Entry points

An entry class starts one protocol. The framework groups an entry by its
runtime, and the protocol is the name of the class.

| Class                                                    | Runtime              | Boots        |
| :------------------------------------------------------- | :------------------- | :----------- |
| `io.valkyrja.application.entry.Http`                     | CGI, or a servlet    | Each request |
| `io.valkyrja.application.entry.Cli`                      | The command line     | Once         |
| `io.valkyrja.application.entry.Grpc`                     | One call, or a test  | Each call    |
| `io.valkyrja.application.entry.exchange.ExchangeHttp`    | The JDK `HttpServer` | Once         |
| `io.valkyrja.application.entry.exchange.ExchangeCgiHttp` | The JDK `HttpServer` | Each request |
| `io.valkyrja.application.entry.jetty.JettyHttp`          | Jetty                | Once         |
| `io.valkyrja.application.entry.jetty.JettyGrpc`          | Jetty                | Once         |
| `io.valkyrja.application.entry.netty.NettyHttp`          | Netty                | Once         |
| `io.valkyrja.application.entry.netty.NettyGrpc`          | Netty                | Once         |
| `io.valkyrja.application.entry.tomcat.TomcatHttp`        | Tomcat               | Once         |
| `io.valkyrja.application.entry.tomcat.TomcatGrpc`        | Tomcat               | Once         |

Each entry class holds static methods that take the config of its protocol. Ten
of the eleven hold a `run` method.

Warning: `io.valkyrja.application.entry.Grpc` holds no `run` method. It holds
`bootstrap(config)`, which returns the application, and `handle(config, call)`,
which dispatches one call.

```java
public final class App {

    public static void main(String[] args) throws IOException {
        ExchangeHttp.run(new HttpConfig());
    }
}
```

The CLI entry takes the arguments of the process as well.

```java
Cli.run(new CliConfig(), args);
```

### The optional adapters

The JDK `HttpServer` needs no dependency, so `ExchangeHttp` is the default. Each
other adapter needs the SDK of its runtime, and the build declares that SDK as
`compileOnly`.

Warning: a `compileOnly` dependency is absent from the published POM, so the SDK
does not reach the classpath of a consumer. An application that runs `JettyHttp`
declares `org.eclipse.jetty:jetty-server` in its own build.

The framework compiles and runs with none of the SDKs present. A consumer pulls
the one runtime that it uses, and no other.

## Configuration

`io.valkyrja.application.data.contract.ConfigContract` holds the settings of
every application.

| Property        | Holds                                                        |
| :-------------- | :----------------------------------------------------------- |
| `namespace`     | The root package of the application                          |
| `dir`           | The base directory                                           |
| `version`       | The version of the application                               |
| `environment`   | The name of the environment                                  |
| `debugMode`     | Whether the application runs in debug mode                   |
| `timezone`      | The default time zone, which the kernel sets at construction |
| `key`           | The application key                                          |
| `dataPath`      | The directory of the generated data classes                  |
| `dataNamespace` | The package of the generated data classes                    |
| `providers`     | The component providers of the application                   |
| `callbacks`     | A callback that runs against the application at boot         |

Three contracts extend it, one for each protocol.

`HttpConfigContract` adds `port`, and one list for each of the seven HTTP
middleware stages. `CliConfigContract` adds `applicationName`,
`defaultCommandName`, and one list for each of the six CLI middleware stages.
`GrpcConfigContract` adds `port`, `maxInboundMessages`, and one list for each of
the seven gRPC middleware stages.

Each contract has a record that implements it: `Config`, `HttpConfig`,
`CliConfig`, and `GrpcConfig`. The no-argument constructor of each record holds
the defaults.

| Config       | Port    | Component provider                        |
| :----------- | :------ | :---------------------------------------- |
| `Config`     | —       | `ApplicationComponentProvider`            |
| `HttpConfig` | `8080`  | `HttpApplicationComponentProvider`        |
| `CliConfig`  | —       | `CliWithHttpApplicationComponentProvider` |
| `GrpcConfig` | `50051` | `GrpcApplicationComponentProvider`        |

`HttpConfig` also holds `LogThrowableCaughtMiddleware` in its
`throwableCaughtMiddleware` list. Every other middleware list is empty.

`GrpcConfig` reads `maxInboundMessages` as
`GrpcConfigContract.DEFAULT_MAX_INBOUND_MESSAGES`, which is `1000`, when the
argument is `null`.

Each record is immutable. The compact constructor copies each list, so a later
write to the source list does not reach the config.

### A config of the application

Write a record that implements the contract of the protocol, and list the
component providers of the application.

```java
public record AppHttpConfig(...) implements HttpConfigContract {}
```

Warning: the container publishes the config under the contract it implements.
`App.bootstrapServices` binds `ConfigContract`, and it binds
`CliConfigContract`, `HttpConfigContract`, or `GrpcConfigContract` when the
config implements that contract. Resolve the config through a contract, and
never through the record.

```java
HttpConfigContract config = container.getSingleton(HttpConfigContract.class);
```

## The bootstrap sequence

`App.start(config)` runs six steps.

1. `defaultExceptionHandler()` runs when `debugMode` is `true`.
2. `appStart()` records the start value from `Microtime.get()`. The
   [support component](../support/README.md) describes the time source.
3. `directory(config.dir())` sets the base path of `Directory`.
4. `getContainer()` builds a `Container`.
5. `getApplication(container, config)` builds a `Valkyrja` kernel, whose
   constructor sets the default time zone from `config.timezone()`.
6. `bootstrapServices(app, container, config)` binds the application and the
   config, runs each config callback, and loads the container data.

`loadContainerData` publishes `ContainerDataContract` when no binding holds it,
and it merges the result into the container.
`io.valkyrja.container.provider.ServiceProvider.publishData` performs the
publication. It reads every service provider of the application, and it
registers each one. The [container component](../container/README.md) describes
the registration.

Warning: an application that binds `ContainerDataContract` before this step
ships a generated data class, and the framework then registers no provider at
all.

Each method of `App` is `public static`, so a runtime that cannot extend the
class still reproduces the sequence.

## The provider hierarchy

`io.valkyrja.application.provider.contract.ComponentProviderContract` declares
six methods. Each one returns the providers of one kind.

```java
List<ComponentProviderContract> getComponentProviders(ApplicationContract app);

List<ServiceProviderContract> getContainerProviders(ApplicationContract app);

List<ListenerProviderContract> getEventProviders(ApplicationContract app);

List<CliRouteProviderContract> getCliProviders(ApplicationContract app);

List<HttpRouteProviderContract> getHttpProviders(ApplicationContract app);

List<GrpcRouteProviderContract> getGrpcProviders(ApplicationContract app);
```

A component provider returns the component providers it depends on from
`getComponentProviders`, and its own providers from the other five methods.

### Loading order

`Valkyrja.getProviders` reads `config.providers()`, and it walks each one. The
walk is depth first, and it adds a nested provider before the provider that
holds it.

Warning: the container holds one publisher for each key, and the provider that
registers last owns the key. List the component provider of the application so
that the walk reaches it last. A provider whose `getComponentProviders` returns
the framework providers meets that rule, because the walk adds the framework
providers first.

```java
public class AppComponentProvider implements ComponentProviderContract {

    @Override
    public List<ComponentProviderContract> getComponentProviders(ApplicationContract app) {
        return List.of(new HttpApplicationComponentProvider());
    }

    @Override
    public List<ServiceProviderContract> getContainerProviders(ApplicationContract app) {
        return List.of(new AppServiceProvider());
    }
}
```

The kernel caches each list after the first call, so the walk runs once.

### The component providers of the framework

| Provider                                  | Loads                                                                                                |
| :---------------------------------------- | :--------------------------------------------------------------------------------------------------- |
| `ApplicationComponentProvider`            | The container and the event components                                                               |
| `HttpApplicationComponentProvider`        | The application component, the four HTTP components, the HTTP routing command, and the log component |
| `CliApplicationComponentProvider`         | The application component, the four CLI components, and the log component                            |
| `CliWithHttpApplicationComponentProvider` | The CLI providers, and the HTTP components                                                           |
| `GrpcApplicationComponentProvider`        | The application component, and the three gRPC components                                             |

`CliWithHttpApplicationComponentProvider` is the default of `CliConfig`, so a
command reaches an HTTP service. The HTTP routing command is `http:list`, and it
lists the routes of the application.

## Accessing the application

`io.valkyrja.application.kernel.contract.ApplicationContract` holds the
container, the six provider lists, and three settings: `getDebugMode`,
`getEnvironment`, and `getVersion`. `App.bootstrapServices` binds the kernel, so
any service resolves it.

```java
ApplicationContract app = container.getSingleton(ApplicationContract.class);
```

`io.valkyrja.application.kernel.Valkyrja` is the default implementation.

## Persistent workers

A worker runtime boots once, and it serves many requests.
`io.valkyrja.application.entry.abstract_.WorkerHttp` and
`io.valkyrja.application.entry.abstract_.WorkerGrpc` hold that lifecycle.

`bootstrap(config)` starts the application, and it returns the kernel. The
container of the kernel is frozen after that call, and nothing writes to it
again.

`dispatch(app, data, request, emitter)` handles one request. It builds a
`ChildContainer` from the frozen parent and the snapshot, wraps the kernel in a
`ChildApplication`, resolves the request handler from the child, and hands the
response to the emitter. The [container component](../container/README.md)
describes the child.

```java
ApplicationContract app = ExchangeHttp.bootstrap(config);
ContainerData data = (ContainerData) app.getContainer().getData();

// For each request:
ExchangeHttp.dispatch(app, data, request, emitter);
```

Capture the snapshot once, after the bootstrap. Each request reads that one
snapshot, and each child copies from it.

`bootstrapParentServices(app)` is empty. Override it to resolve an expensive
shared service at startup, so the first request does not pay for it.

`bootstrapThrowableHandler(app, container)` is empty as well. The framework
ships no error display handler. The
[throwable component](../throwable/README.md) describes the handler contract.

## Directories

`io.valkyrja.application.directory.Directory` builds a path from the base path.
`App.start` sets the base path from `config.dir()`.

| Method                            | Returns                                 |
| :-------------------------------- | :-------------------------------------- |
| `basePath(path)`                  | `<base>/<path>`                         |
| `appPath(path)`                   | `<base>/app/<path>`                     |
| `dataPath(path)`                  | `<base>/data/<path>`                    |
| `publicPath(path)`                | `<base>/public/<path>`                  |
| `resourcesPath(path)`             | `<base>/resources/<path>`               |
| `srcPath(path)`                   | `<base>/src/<path>`                     |
| `storagePath(path)`               | `<base>/storage/<path>`                 |
| `frameworkStoragePath(path)`      | `<base>/storage/framework/<path>`       |
| `frameworkStorageCachePath(path)` | `<base>/storage/framework/cache/<path>` |
| `logsStoragePath(path)`           | `<base>/storage/logs/<path>`            |

Each method takes a nullable path. Pass `null` for the directory itself.

```java
String logs = Directory.logsStoragePath(null);
```

## Framework information

`io.valkyrja.application.constant.ApplicationInfo` holds three constants:
`VERSION`, `VERSION_BUILD_DATE_TIME`, and `ASCII`. The release automation writes
the first two.

## Exceptions

The component ships `ApplicationRuntimeException` and
`ApplicationInvalidArgumentException`. Both are abstract, and both implement
`ApplicationThrowable`. The component throws neither one today. The
[throwable component](../throwable/README.md) describes the hierarchy.
