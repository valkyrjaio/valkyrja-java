# Type

## Introduction

The type component holds the shared types that more than one component reads. It
holds three: a cast descriptor for a route parameter, a contract for a type that
converts a value, and a contract for a type that presents itself as a map and as
a list.

The component publishes no container binding, and it holds no configuration.

## Cast

`io.valkyrja.type.data.Cast` describes how the router converts a route
parameter. The type is a container binding key, so the container builds the
type.

```java
public Cast(Class<? extends TypeContract> type, boolean convert, boolean isArray);

public Cast(Class<? extends TypeContract> type);
```

The one-argument constructor sets `convert` to `true` and `isArray` to `false`.
Three getters read the state back: `getType()`, `isConvert()`, and `isArray()`.

Warning: no framework code reads `isArray()`. The caster converts each value one
at a time, whatever the flag holds.

A route parameter holds an optional cast. The CLI parameter and the HTTP
parameter both read it through `hasCast()` and `getCast()`, and `withCast(Cast)`
returns a copy that holds a new one.

## TypeContract

`io.valkyrja.type.contract.TypeContract` is the contract for a value that the
framework converts.

```java
public interface TypeContract {

    @Nullable Object asValue();

    @Nullable Object asFlatValue();

    TypeContract modify(UnaryOperator<@Nullable Object> closure);
}
```

PHP declares a static `fromValue()` on this contract. Java cannot call a static
method on a variable class, so this port leaves the method out. The application
binds an implementation, and the container builds it. See
[STATIC_METHODS.md](https://github.com/valkyrjaio/architecture/blob/26.x/STATIC_METHODS.md).

Warning: no framework code calls `asFlatValue()` or `modify()`. The caster calls
`asValue()` only. The contract mirrors PHP's `TypeContract`, which every PHP
value object implements.

This port ships no implementation of the contract. An application supplies its
own type:

```java
import io.valkyrja.type.constant.CastArgument;

container.bind(Slug.class, (c, arguments) -> new Slug(String.valueOf(arguments.get(CastArgument.VALUE))));

var parameter = new ArgumentParameter("target", "The target").withCast(new Cast(Slug.class));
```

### Where the framework applies a cast

`io.valkyrja.cli.routing.caster.Caster` applies the cast, and the parameter
applies nothing. The container publishes it under
`io.valkyrja.cli.routing.caster.contract.CasterContract`. The caster holds the container, so the data object needs none.
`getCastValues()` on the caster asks the container for the type that `getType()`
names, and it passes the raw value under the key `CastArgument.VALUE`. It
returns the converted value when `isConvert()` is `true`, and the type itself
when `isConvert()` is `false`. A parameter that holds no cast returns each raw
value. The [cli component](../cli/README.md) describes the parameters.

Warning: `getService()` reads only a service binding. An alias, and an instance
that `setSingleton` holds, raise `ContainerInvalidReferenceException`. Register
a cast type with `bind`.

`bindSingleton` also registers a callable, so a cast type that `bindSingleton`
registers still resolves. `getService()` skips the singleton cache, so the
caster builds one instance for each value. That is not the lifetime that
`bindSingleton` states, which is the second reason a cast type takes `bind`.

The HTTP matcher applies the cast the same way. `Matcher.castMatchValue` asks
`getService()` for the type that `getType()` names, and it passes the matched
text under the key `CastArgument.VALUE`. The matcher holds the container, so no
data object reaches it. The [http component](../http/README.md) describes the
matcher.

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
