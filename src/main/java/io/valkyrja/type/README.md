# Type

## Introduction

The type component holds the shared types that more than one component reads. It
holds two: a cast descriptor for a route parameter, and a contract for a type
that presents itself as a map and as a list.

The component publishes no container binding, and it holds no configuration.

## Cast

`io.valkyrja.type.data.Cast` describes how the router converts a matched route
parameter.

```java
public Cast(String type, boolean convert, boolean isArray);

public Cast(String type);
```

The one-argument constructor sets `convert` to `true` and `isArray` to `false`.
Three getters read the state back: `getType()`, `isConvert()`, and `isArray()`.

A route parameter holds an optional cast.
`io.valkyrja.http.routing.data.Parameter` reads it through `hasCast()` and
`getCast()`, and `withCast(Cast)` returns a copy that holds a new one.

Warning: `Matcher.castMatchValue` returns the matched value without a change, so
the framework matcher applies no conversion. `Cast` carries the intent of the
route, and a subclass of `Matcher` performs the conversion. The
[http component](../http/README.md) describes the matcher.

## ArrayableContract

`io.valkyrja.type.enum_.contract.ArrayableContract` declares two methods.

```java
public interface ArrayableContract {

    Map<String, String> asMap();

    List<String> values();
}
```

`io.valkyrja.http.struct.contract.StructContract` extends the contract, so every
request struct and every response struct implements the two methods.

- A request struct reads `values()`. The names in the list select the parameters
  that the struct takes from the request.
- A response struct reads `asMap()`. Each entry maps a key of the data to the
  key that the response holds.

The [http component](../http/README.md) describes the structs.
