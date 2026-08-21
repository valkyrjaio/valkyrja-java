# Event Dispatching

## Introduction

The event component holds a dispatcher, a collection of listeners, and three
optional contracts that change how the dispatcher treats an event.

An event is a plain object. A listener connects one event class to one handler.
The dispatcher reads the class of the event, and it calls each handler that the
collection holds for that class.

Warning: the component publishes no container binding.
`io.valkyrja.event.provider.EventComponentProvider` returns an empty list from
every method. An application that dispatches an event constructs the dispatcher,
or binds `EventDispatcherContract` in a service provider of its own. The
[container component](../container/README.md) describes a service provider.

## Defining an event

An event is a class that the application writes. It takes the data of one
occurrence, and it extends no framework type.

```java
public record UserRegistered(String userId) {}
```

Three optional contracts change the behavior of the dispatcher.

| Contract                           | The dispatcher                                                    |
| :--------------------------------- | :---------------------------------------------------------------- |
| `ArgumentsCapableEventContract`    | Calls `setArguments` after it resolves the event                  |
| `DispatchCollectableEventContract` | Calls `addDispatch` with the result of each handler               |
| `StoppableEventContract`           | Stops after a listener when `isPropagationStopped` returns `true` |

Each contract lives in `io.valkyrja.event.contract`.

## Defining a listener

`io.valkyrja.event.data.contract.ListenerContract` holds three values: the event
class, a unique name, and the handler. `io.valkyrja.event.data.Listener` is the
default implementation, and each `with` method returns a new instance.

The handler is a `BiFunction<ContainerContract, Map<String, Object>, Object>`.
The dispatcher passes the container, and a map that holds one entry with the key
`event`.

```java
ListenerContract listener =
        new Listener(
                UserRegistered.class,
                "app.listener.welcomeEmail",
                (container, arguments) -> {
                    UserRegistered event = (UserRegistered) arguments.get("event");
                    container.getSingleton(LoggerContract.class)
                            .info("A user registered.", Map.of("userId", event.userId()));

                    return null;
                });
```

The name identifies the registration. Two listeners for one event take two
names, because the collection keys a listener by its name.

## The listener collection

`io.valkyrja.event.collection.ListenerCollection` holds every listener. It keeps
two maps: the event class to the names of its listeners, and the name to the
listener.

```java
ListenerCollectionContract collection = new ListenerCollection();

collection.addListener(listener);
```

| Method                                    | Does                                                          |
| :---------------------------------------- | :------------------------------------------------------------ |
| `addListener(listener)`                   | Adds the listener for the event class it holds                |
| `removeListener(listener)`                | Removes the listener by its name                              |
| `removeListenerById(name)`                | Removes the listener with that name                           |
| `hasListener(listener)`                   | Reports whether the name of the listener is registered        |
| `hasListenersForEvent(event)`             | Reports whether the class of the event holds a listener       |
| `getListenersForEvent(event)`             | Returns the listeners of that event class, in the order added |
| `setListenersForEvent(event, listeners…)` | Adds each listener with the class of the event                |
| `removeListenersForEvent(event)`          | Removes every listener of that event class                    |
| `getListeners()`                          | Returns every listener                                        |
| `getEvents()`                             | Returns every event class that holds a listener               |
| `getEventsWithListeners()`                | Returns each event class with its listeners                   |

Each method takes an event instance, and each one has a variant that takes the
event class. The variant ends in `ById`, and it takes a `Class<?>`.

Warning: `setListenersForEvent` adds each listener. It removes no listener that
the collection holds already. Call `removeListenersForEvent` first to replace the
listeners of an event.

Warning: the collection matches the exact class of the event. A listener that is
registered for a parent class does not run for a subclass.

## Dispatching an event

`io.valkyrja.event.dispatcher.EventDispatcher` implements
`EventDispatcherContract`. The constructor takes the collection and the
container. The no-argument constructor builds an empty collection and an empty
container.

```java
EventDispatcherContract dispatcher = new EventDispatcher(collection, container);

dispatcher.dispatch(new UserRegistered("42"));
```

| Method                                           | Does                                                               |
| :----------------------------------------------- | :----------------------------------------------------------------- |
| `dispatch(event)`                                | Calls every listener of the event class                            |
| `dispatchIfHasListeners(event)`                  | Calls `dispatch` when the class holds a listener                   |
| `dispatchById(eventId, arguments)`               | Resolves the event from the container, then dispatches             |
| `dispatchByIdIfHasListeners(eventId, arguments)` | Resolves the event, and dispatches when the class holds a listener |
| `dispatchListeners(event, listeners…)`           | Calls the listeners that the caller names                          |
| `dispatchListener(event, listener)`              | Calls one listener                                                 |

Each method returns the event. A listener that changes the state of the event
changes what the caller reads back.

`dispatchListener` discards the return value of the handler, unless the event
implements `DispatchCollectableEventContract`. The dispatcher then passes the
value to `addDispatch`.

## Dispatching by class

`dispatchById` resolves the event from the container, and it passes the
arguments to the binding.

```java
dispatcher.dispatchById(UserRegistered.class, Map.of("userId", "42"));
```

Warning: the container builds nothing that a binding does not describe, so the
event class needs a binding. The container throws
`ContainerInvalidReferenceException` for an event class that no binding holds.

Warning: the dispatcher throws `EventInvalidEventException` when the container
returns an object that is not an instance of the event class.

An event that implements `ArgumentsCapableEventContract` receives the same
arguments through `setArguments`, after the container returns it.

## Listener providers

`io.valkyrja.event.provider.contract.ListenerProviderContract` declares two
methods.

```java
List<Class<?>> getListenerClasses();

List<ListenerContract> getListeners();
```

A component provider returns its listener providers from `getEventProviders`, and
`ApplicationContract.getEventProviders` collects them from every component. The
[application component](../application/README.md) describes the collection.

Warning: no component of the framework reads that list into a collection. An
application that uses a listener provider reads
`app.getEventProviders()` itself, and it adds each listener to its own
collection.

## Snapshots

`io.valkyrja.event.data.EventData` is an immutable record of the two maps of the
collection. `getData()` returns a snapshot, and `setFromData(data)` replaces the
state of the collection with a snapshot.

Warning: `setFromData` clears both maps before it reads the snapshot. It merges
nothing.

## Exceptions

`EventInvalidEventException` extends `EventInvalidArgumentException`, and the
component also ships `EventRuntimeException`. Each one implements
`EventThrowable`. The [throwable component](../throwable/README.md) describes the
hierarchy.
