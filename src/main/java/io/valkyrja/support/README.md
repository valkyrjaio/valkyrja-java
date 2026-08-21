# Support

## Introduction

The support component holds the small utilities that the other components use.
It holds a time source that a test freezes, and a base class that writes a
generated file.

The component publishes no container binding, and it holds no configuration.

## Time

`io.valkyrja.support.time.Time` reads the clock. Code that calls `Time.get()`
instead of the clock is testable with a fixed value.

```java
Time.freeze();   // Freeze the clock at the current value
Time.unfreeze(); // Resume the real clock
Time.get();      // Return the frozen value, or the current value
```

Warning: `Time` reads `System.nanoTime()`, and `System.nanoTime()` has an
arbitrary origin. The value measures elapsed seconds. The value is not a Unix
timestamp, so never write it as a date.

`freeze()` takes no argument. It records the current value, and `get()` returns
that value until `unfreeze()` runs.

`io.valkyrja.support.time.Microtime` holds the same three methods at microsecond
precision. `Microtime.get()` returns a `Long`, and `Time.get()` returns a
`Double`.

A frozen value is global state, so a test releases the value when the test ends.

```java
@AfterEach
void tearDown() {
    Time.unfreeze();
}
```

Warning: each class holds a private constructor, so an application cannot
construct one and cannot extend one. Java hides a static method in a subclass
instead of overriding it, so a subclass cannot substitute a different clock. To
read a different clock, call that clock directly.

## The file generator

`io.valkyrja.support.generator.contract.FileGeneratorContract` declares two
methods.

```java
public interface FileGeneratorContract {

    String generateFileContents();

    void generateFile();
}
```

`io.valkyrja.support.generator.abstract_.FileGenerator` implements
`generateFile()`. The constructor takes the file path. `generateFile()` calls
`generateFileContents()`, creates the parent directories of the path, and writes
the contents as UTF-8.

Warning: `generateFile()` throws a `java.lang.RuntimeException` when the write
fails. The message of the exception names the file path.

A subclass implements `generateFileContents()`, and it inherits the write.
`io.valkyrja.http.server.generator.ResponseFileGenerator` is the one subclass
the framework ships. It writes the status code, the reason phrase, the protocol
version, each header, and the body of a response. The
[http component](../http/README.md) describes the middleware that calls it.
