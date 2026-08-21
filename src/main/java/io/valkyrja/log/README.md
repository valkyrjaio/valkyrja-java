# Log

## Introduction

The log component holds one logging contract and two implementations. The
default writes a line to a dated file. The other discards every call.

The component adds no dependency to the classpath. It uses the JDK only.

## The logger contract

`io.valkyrja.log.logger.contract.LoggerContract` declares one method for each
level, and two more.

```java
void debug(String message, Map<String, Object> context);

void info(String message, Map<String, Object> context);

void notice(String message, Map<String, Object> context);

void warning(String message, Map<String, Object> context);

void error(String message, Map<String, Object> context);

void critical(String message, Map<String, Object> context);

void alert(String message, Map<String, Object> context);

void emergency(String message, Map<String, Object> context);

void log(LogLevel level, String message, Map<String, Object> context);

void throwable(Throwable throwable, String message, Map<String, Object> context);
```

Every method takes a context map. Pass `Map.of()` when there is no context.

```java
LoggerContract logger = container.getSingleton(LoggerContract.class);

logger.info("The route matched.", Map.of("path", request.getUri().getPath()));
```

## The log level

`io.valkyrja.log.enum_.LogLevel` holds eight cases: `DEBUG`, `INFO`, `NOTICE`,
`WARNING`, `ERROR`, `CRITICAL`, `ALERT`, and `EMERGENCY`. `getValue()` returns
the lowercase name, and the file logger writes that value.

`io.valkyrja.log.logger.abstract_.Logger` implements `log`. It reads the level
and calls the method for that level, so an implementation writes the eight level
methods and inherits `log`.

Warning: `log` throws `LogInvalidLogLevelException` for a `null` level.

## FileLogger

`io.valkyrja.log.logger.FileLogger` appends one line for each call. The
no-argument constructor writes to `valkyrja-<date>.log` in the logs storage
directory, which is `<base>/storage/logs`. A new file starts each day. The
second constructor takes a `java.nio.file.Path`, and the logger writes to that
file.

```java
LoggerContract logger = new FileLogger(Path.of("/var/log/example.log"));
```

Each entry holds the timestamp, the level, and the message. The logger appends
the context when the map holds an entry.

```text
[2026-08-20 09:15:00] info: The route matched. {path=/example}
```

`throwable` writes at the `ERROR` level. It appends the stack trace of the
throwable to the message, so the entry covers more than one line.

The logger creates the parent directories of the file, and it opens the file
with `CREATE` and `APPEND`.

Warning: `FileLogger` throws `LogFileWriteException` when the write fails. The
message names the file. The [throwable component](../throwable/README.md)
describes the hierarchy this exception belongs to.

## NullLogger

`io.valkyrja.log.logger.NullLogger` implements every method with an empty body.
Bind it for a test, and for an environment that writes no log.

## Container bindings

`io.valkyrja.log.provider.LogServiceProvider` publishes three bindings.

| Binding key      | Resolves to                             |
| :--------------- | :-------------------------------------- |
| `LoggerContract` | The `FileLogger` singleton              |
| `FileLogger`     | A `FileLogger` for today's default file |
| `NullLogger`     | A `NullLogger`                          |

Each binding is a singleton, and each publisher runs on the first resolution of
its key. The [container component](../container/README.md) describes the
deferred publisher.

`io.valkyrja.log.provider.LogComponentProvider` returns the service provider
from `getContainerProviders`, and an empty list from every other method.

## Binding a different logger

The framework ships one default for each contract it publishes, and the
application supplies the rest. To log through another backend, implement
`LoggerContract` and publish the implementation from a service provider of the
application.

```java
public class AppLogServiceProvider implements ServiceProviderContract {

    @Override
    public Map<Class<?>, Consumer<ContainerContract>> publishers() {
        return Map.of(LoggerContract.class, AppLogServiceProvider::publishLogger);
    }

    public static void publishLogger(ContainerContract container) {
        container.setSingleton(LoggerContract.class, new AppLogger());
    }
}
```

The container holds one publisher for each key, and the last provider that
registers a key wins. List the component provider of the application after the
framework providers, so the publisher of the application replaces the default.
The [application component](../application/README.md) describes the order in
which the providers register.
