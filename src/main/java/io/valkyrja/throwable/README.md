# Throwable

## Introduction

The throwable component holds the root of the framework exception hierarchy. It
declares one contract, four base throwables, and a handler base that computes a
trace code. Every other component derives its own exceptions from these bases.

The component publishes no container binding, and it holds no configuration.

## The throwable contract

`io.valkyrja.throwable.contract.ThrowableContract` declares one method.

```java
public interface ThrowableContract {

    String getTraceCode();
}
```

Warning: `ThrowableContract` is an interface, and `java.lang.Throwable` is a
class, so the contract does not extend it. A `catch` clause names a class that
extends `java.lang.Throwable`, so a `catch` clause cannot name
`ThrowableContract`. Catch one of the base classes below, and test for the
contract with `instanceof`.

```java
try {
    logger.info("The application started.", Map.of());
} catch (LogRuntimeException exception) {
    String traceCode = exception.getTraceCode();
}
```

## The base throwables

Each base class extends a native Java throwable and implements the contract.

| Class                      | Extends                              | Checked   |
| :------------------------- | :----------------------------------- | :-------- |
| `Exception`                | `java.lang.Exception`                | Checked   |
| `RuntimeException`         | `java.lang.RuntimeException`         | Unchecked |
| `InvalidArgumentException` | `java.lang.IllegalArgumentException` | Unchecked |
| `TypeError`                | `java.lang.RuntimeException`         | Unchecked |

All four live in `io.valkyrja.throwable.exception`, except `TypeError`, which
lives in `io.valkyrja.throwable.error`. Each one takes a message, or a message
and a cause.

`TypeError` extends `java.lang.RuntimeException` rather than `java.lang.Error`.
An error of the application is not an error of the JVM.

Every component exception in the framework extends `RuntimeException` or
`InvalidArgumentException`, so no framework method declares a `throws` clause.

Each of the three exception classes holds a static `throwException(String)`
method that constructs the exception and throws it.

## The trace code

`io.valkyrja.throwable.handler.abstract_.ThrowableHandler` computes the trace
code.

```java
public static String getTraceCode(Throwable throwable);
```

The method reads the class name of the throwable and the string form of its
stack trace. It returns the MD5 digest of the two, as lowercase hexadecimal.
Each base class returns that value from `getTraceCode()`.

Warning: the trace code changes when the stack trace changes. It correlates a
log entry with one failure point. It does not identify an exception class.

## The throwable handler

`io.valkyrja.throwable.handler.contract.ThrowableHandlerContract` declares the
handler, and `ThrowableHandler` implements the trace code for a subclass.

The framework ships no concrete handler. `App.bootstrapThrowableHandler` does
nothing, and an application overrides it in its own entry class to register a
handler for debug mode. The [application component](../application/README.md)
describes the entry classes.

## Component throwables

A component holds its own throwable segment, and the names follow the framework
rule. The names prepend the component, and the sub-component, until each name is
unique across the framework.

- A contract interface extends `ThrowableContract`. The log component declares
  `LogThrowable`.
- An abstract base extends a base class above and implements that contract. The
  log component declares `LogRuntimeException` and `LogInvalidArgumentException`.
- A concrete exception extends the abstract base. The log component declares
  `LogFileWriteException`.

Every component ships both abstract bases, and a component ships a base that it
does not throw today.

Warning: an abstract base is never thrown. Throw a concrete exception, so the
`catch` clause of a caller can name the one failure it handles.
