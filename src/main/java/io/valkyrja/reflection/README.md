# Reflection

## Introduction

The reflection component holds a cache over the Java reflection API. It resolves
an instance, a no-argument constructor, and a method once, and it returns the
same object on each later call.

Warning: the container resolves a binding with a lambda, and never with
reflection. Read the [container component](../container/README.md) before you
reach for this component to build a service.

The component publishes no container binding, and it holds no configuration.

## The Reflection support class

`io.valkyrja.reflection.support.Reflection` holds three static methods.

```java
public static <T> T instantiate(Class<T> className);

public static <T> Constructor<T> constructor(Class<T> className);

public static Method method(Class<?> cls, String name, Class<?>... paramTypes);
```

`instantiate` returns one instance for each class. It calls the no-argument
constructor on the first call, and it returns the cached instance on each later
call.

Warning: `instantiate` is not a factory. Two callers that ask for the same class
get the same object, so never call it for a type that holds mutable state.

`constructor` returns the declared no-argument constructor of the class.
`method` returns a public method, and the parameter types select the overload.

Each cache is a `ConcurrentHashMap`. The instance cache and the constructor
cache use the class as the key. The method cache builds a string key from the
class name, the method name, and the parameter type names, so two overloads hold
two entries.

Warning: the cache holds every class a caller asks for, and it never evicts an
entry. `Reflection` is for a type the application resolves once. It is not for a
type the application creates for each request.

## Throwables

`Reflection` throws
`io.valkyrja.reflection.throwable.exception.ReflectionInvalidClassToInstantiateException`
for each of three failures.

| Failure                                       | Message                                   |
| :-------------------------------------------- | :---------------------------------------- |
| The class declares no no-argument constructor | `Failed to get constructor for <class>`   |
| The constructor throws, or denies access      | `Failed to instantiate <class>`           |
| The class holds no such public method         | `Failed to get method <name> for <class>` |

The hierarchy follows the framework rule. `ReflectionThrowable` extends
`ThrowableContract`, and every exception in the component implements it. The two
abstract bases are `ReflectionRuntimeException` and
`ReflectionInvalidArgumentException`. The
[throwable component](../throwable/README.md) holds the roots they extend.

## Component registration

`io.valkyrja.reflection.provider.ReflectionComponentProvider` implements
`ComponentProviderContract`. Each method returns an empty list, so the component
contributes no container binding, no listener, and no route. The
[application component](../application/README.md) describes how a component
provider loads.
