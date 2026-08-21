# The Container

## Introduction

The container registers and resolves every service of a Valkyrja application. It
holds an explicit binding model, three service types, and deferred loading.

A binding key is a `Class` object, so the compiler checks it. The container
returns the type of the key, and the caller needs no cast.

This document covers each feature with an example.

- [Service types](#service-types) — the three registration types.
- [Binding services](#binding-services) — the four binding methods.
- [Resolving services](#resolving-services) — the four resolution methods.
- [Inspecting the container](#inspecting-the-container) — the state methods.
- [Service providers](#service-providers) — the registration convention.
- [Container data](#container-data) — the snapshot of every binding.
- [Child containers](#child-containers) — the isolation of one request.
- [Exceptions](#exceptions) — what the container throws, and when.

## Contracts

The project calls an interface a contract. A type whose name ends in `Contract`
is an interface. Bind against a contract, and not against a concrete class. The
framework does the same.

## Deferred loading

A service is deferred. The container constructs nothing at boot. It holds a map
of the binding key and the callback that builds the service. The container runs
the callback on the first resolution of that key. The boot cost does not grow
with the number of registered services.

## Service types

The container holds three registration types.

**Singleton.** The container builds one instance on the first resolution, and it
returns that instance on each later call. Use a singleton for a shared service,
such as the router, the event dispatcher, or the logger.

**Service.** The container builds a new instance on each resolution. Use a
service for a type that holds the state of one caller.

**Alias.** A key that maps to another key. A resolution of the alias resolves
the target.

## Binding services

### The factory signature

`bind` and `bindSingleton` take a
`BiFunction<ContainerContract, Map<String, Object>, T>`. The container passes
itself as the first argument, so the factory resolves its own dependencies. The
second argument holds the arguments that the caller passes to `get` or
`getService`.

```java
container.bind(
        MatcherContract.class,
        (c, arguments) -> new Matcher(c.getSingleton(RouteCollectionContract.class)));
```

### bind

`bind` registers a factory. The container calls the factory on each resolution,
and it caches nothing.

### bindSingleton

`bindSingleton` registers a factory and marks the key as a singleton. The
container calls the factory on the first resolution, and it caches the result.

```java
container.bindSingleton(
        MatcherContract.class,
        (c, arguments) -> new Matcher(c.getSingleton(RouteCollectionContract.class)));
```

### bindAlias

`bindAlias` maps one key to another.

Warning: the signature is `bindAlias(Class<T> alias, Class<T> id)`, and `Class`
is invariant. A call that names two different types does not compile. Cast the
target to `Class<T>` to bind the alias.

```java
@SuppressWarnings("unchecked")
private static <T> Class<T> raw(Class<?> type) {
    return (Class<T>) type;
}

container.bindAlias(MatcherContract.class, raw(Matcher.class));
```

### setSingleton

`setSingleton` registers an instance that the caller built. The container caches
the instance, and it calls no factory.

```java
container.setSingleton(ConfigContract.class, config);
```

Each of the four methods returns the container, so a caller chains the calls.

### Every service needs a binding

The container resolves a key through a cached instance, a bound factory, or an
alias. It builds nothing that a binding does not describe.

Warning: `get` throws `ContainerInvalidReferenceException` for a key that none
of the three resolves. The container does not construct the class that the key
names.

```java
// Wrong — nothing binds the middleware, so the container throws.
handler.add(AppAuthMiddleware.class);
```

```java
// Right — the binding describes the middleware, and the handler resolves it.
container.bindSingleton(
        AppAuthMiddleware.class,
        (c, arguments) -> new AppAuthMiddleware(c.getSingleton(LoggerContract.class)));

handler.add(AppAuthMiddleware.class);
```

The rule holds for every class that a config or a route names by class object.
It covers a middleware, a request struct, and a command. One place states how
the framework builds each service.

## Resolving services

`get(Class<T> id)` and `get(Class<T> id, Map<String, Object> arguments)` read a
cached singleton first, then a service factory, then an alias. Use `get` when
the registration type of the key is unknown.

```java
MatcherContract matcher = container.get(MatcherContract.class);
```

`getSingleton(Class<T> id)` resolves a singleton. The first call runs the
binding or the publisher, and it caches the result.

`getService(Class<T> id, Map<String, Object> arguments)` resolves a service
factory, and it passes the arguments to that factory.

`getAliased(Class<T> id, Map<String, Object> arguments)` resolves an alias.

Warning: each of the four methods throws `ContainerInvalidReferenceException`
when it resolves nothing. `getSingleton` throws for a key that a factory binds
as a service, and `getService` throws for a key that holds a cached instance.
Call the method that matches how the provider bound the key.

## Inspecting the container

| Method                    | Returns `true` when                                           |
| :------------------------ | :------------------------------------------------------------ |
| `has(id)`                 | A callback, a singleton, a service, or an alias holds the key |
| `isAlias(id)`             | The key maps to another key                                   |
| `isService(id)`           | A factory holds the key                                       |
| `isSingleton(id)`         | A singleton binding or a cached instance holds the key        |
| `isSingletonInstance(id)` | A cached instance holds the key                               |
| `isSingletonBinding(id)`  | A singleton binding holds the key                             |

`isSingletonInstance` reports a built instance, and `isSingletonBinding` reports
a registration. Read `isSingletonInstance` to find what the container built
already, and never to find what it can build.

Warning: the two are not exclusive. `bindSingleton` writes the key into the
registration map, and the first resolution adds the instance without removing
the registration, so both methods then return `true`.

## Service providers

A service provider states which keys it publishes, and how each one is built.

### The publishers map

`io.valkyrja.container.provider.contract.ServiceProviderContract` declares one
method.

```java
Map<Class<?>, Consumer<ContainerContract>> publishers();
```

Each entry maps a key to the publisher of that key. The publisher takes the
container, and it binds the key.

```java
public class LogServiceProvider implements ServiceProviderContract {

    @Override
    public Map<Class<?>, Consumer<ContainerContract>> publishers() {
        return Map.of(
                LoggerContract.class, LogServiceProvider::publishLogger,
                FileLogger.class, LogServiceProvider::publishFileLogger);
    }

    public static void publishLogger(ContainerContract container) {
        container.setSingleton(LoggerContract.class, container.getSingleton(FileLogger.class));
    }
}
```

A publisher resolves another key from the container, and the container publishes
that key first. The example above resolves `FileLogger`, so `publishFileLogger`
runs before `publishLogger` returns.

### Deferred publication

`container.register(provider)` reads the publishers map, and it stores each
callback. It builds nothing.

The container runs a callback on the first resolution of its key, and it marks
the key as published. A second resolution reads the binding that the callback
made.

Warning: the container holds one callback for each key, so the provider that
registers last owns the key. List the component provider of the application
after the framework providers to replace a framework default.

### The Provides annotation

`io.valkyrja.container.annotation.Provides` marks a publisher method with the
key it publishes.

```java
@Provides(ContainerDataContract.class)
public static void publishData(ContainerContract container) {
```

The annotation is a metadata marker. The framework reads no annotation at
runtime. The `sindri` build tool reads it to generate the cache.

### Wiring a provider into the application

A component provider returns its service providers from `getContainerProviders`.
The application config lists the component providers. The
[application component](../application/README.md) describes the config and the
order in which the providers load.

```java
public class LogComponentProvider implements ComponentProviderContract {

    @Override
    public List<ServiceProviderContract> getContainerProviders(ApplicationContract app) {
        return List.of(new LogServiceProvider());
    }
}
```

`io.valkyrja.container.provider.ServiceProvider` performs the registration. Its
`publishData` publisher reads every service provider of the application, calls
`register` for each one, and stores the result as `ContainerDataContract`.

## Container data

`io.valkyrja.container.data.ContainerData` is an immutable record of four maps.

| Component    | Holds                        |
| :----------- | :--------------------------- |
| `aliases`    | The alias key and its target |
| `callbacks`  | The key and its publisher    |
| `services`   | The key and its factory      |
| `singletons` | Each key that is a singleton |

The compact constructor copies each map, so a later write to the source map does
not reach the record.

`getData()` returns a snapshot of the container. `setFromData(data)` merges a
snapshot into the container. `App.loadContainerData` calls both at boot. An
application that ships a generated data class binds it as
`ContainerDataContract` before boot, and the container then registers no
provider at all.

Warning: `ContainerData` holds no built instance. A snapshot describes how to
build each service, and it never holds the service.

## Child containers

A worker runtime boots the application once, and it serves many requests. A
request that writes to the container of the worker changes what the next request
reads. A child container removes that risk.

The parent container is frozen after boot. Each request builds a child, resolves
through the child, and discards the child. Every write reaches the child only.

### The two implementations

`io.valkyrja.container.manager.ChildContainer` takes the parent as a
`ContainerContract`, and a `ContainerData` snapshot of the parent. It copies the
`singletons` map and the `callbacks` map from the snapshot, and it reaches every
other binding through the contract.

`io.valkyrja.container.manager.NativeChildContainer` takes the parent as a
concrete `Container`. It copies no map, and it reads the fields of the parent
directly.

Both extend `Container`, so both hold the full contract.

### Resolution order

A child resolves a singleton in three steps.

1. The cached instance of the child.
2. The cached instance of the parent.
3. The singleton binding, which the child builds and caches in the child.

A child resolves a service, and an alias, from its own maps first, and from the
parent second.

Warning: the factory runs with the child as its argument. A dependency that the
factory resolves therefore comes from the child, and the instance it builds
stays in the child.

### Using a child container

`WorkerHttp.dispatch` builds one child for each request.

```java
ContainerContract childContainer = new ChildContainer(app.getContainer(), data);
```

`data` is the snapshot that the worker captured after boot, and every request
reuses that one snapshot. The [application component](../application/README.md)
describes the worker entry classes.

## Exceptions

| Exception                                  | The container throws it when                             |
| :----------------------------------------- | :------------------------------------------------------- |
| `ContainerInvalidReferenceException`       | A resolution finds no instance, no factory, and no alias |
| `ContainerInvalidPublishCallbackException` | A publishers map holds a key with no callback            |

`ContainerInvalidReferenceException` extends
`ContainerInvalidArgumentException`, and
`ContainerInvalidPublishCallbackException` extends `ContainerRuntimeException`.
Both are unchecked. The [throwable component](../throwable/README.md) describes
the hierarchy.
