# gRPC Services

## Introduction

The gRPC component turns an inbound call into a response. It holds four
sub-components.

| Sub-component | Holds                                                                                                |
| :------------ | :--------------------------------------------------------------------------------------------------- |
| `message`     | The call, the response, the status, the metadata, the deadline, the cancellation token, and the peer |
| `routing`     | The route, the service map, the collector, and the router                                            |
| `middleware`  | The seven middleware stages and their handlers                                                       |
| `server`      | The service handler and the adapter contract                                                         |

The component references no gRPC library and no generated protobuf type. A
message crosses the boundary as an `Object`, and the handler of the application
decodes it. An adapter connects a gRPC server to the component.

Warning: this component has no counterpart in the PHP port. The shape follows
the [http component](../http/README.md), and the two read alike.

## The service handler

`io.valkyrja.grpc.server.handler.contract.ServiceHandlerContract` runs one call.
`ServiceHandler` is the default implementation.

| Method                      | Does                                         |
| :-------------------------- | :------------------------------------------- |
| `handle(call)`              | Runs the `CallReceived` stage and the router |
| `sending(call, response)`   | Runs the `SendingResponse` stage             |
| `terminate(call, response)` | Runs the `ResponseSent` stage                |
| `run(call)`                 | Calls `handle`, and then `sending`           |

`handle` catches every `Throwable`. A `CancelledException` returns a cancelled
response, and every other throwable returns a response with the `INTERNAL`
status. The `ThrowableCaught` stage then runs with that response.

Warning: the handler wraps the throwable in a `java.lang.RuntimeException` and
throws that, when the application runs in debug mode. A `catch` clause that
names the original type does not match it, so read the cause.
`GrpcServerServiceProvider` reads `app.getDebugMode()` and passes it to the
constructor.

## The router

`io.valkyrja.grpc.routing.dispatcher.Router` looks the method of the call up in
the service map. The service map is
`io.valkyrja.grpc.routing.collection.RouteCollection`, which the container
publishes as `RouteCollectionContract`. The key is the full method, which reads
`/package.Service/Method`, and the match is exact.

1. A method that the service map does not hold returns an `UNIMPLEMENTED`
   response, and the `RouteNotMatched` stage runs.
2. The router appends the middleware of the route to five stage handlers, and it
   binds the call as `ServiceCallContract`.
3. The router runs the cancellation check. A cancelled call returns without the
   dispatch.
4. The router runs the `RouteMatched` stage, and it then binds the route that
   the stage returned as `RouteContract`. A middleware that returns a response
   stops the dispatch.
5. The router calls the handler of the route.
6. The router runs the cancellation check again, and then the `RouteDispatched`
   stage.

The handler is a
`BiFunction<ContainerContract, RouteContract, ServiceResponseContract>`.

## Defining a service

A service is a class, and each RPC is a method of that class. The annotations
live in `io.valkyrja.grpc.routing.attribute`.

```java
@Service(service = "example.Greeter")
public final class AppGreeterController {

    @Method(name = "SayHello")
    @Middleware(name = AppAuthMiddleware.class)
    public ServiceResponseContract sayHello(ContainerContract container, RouteContract route) {
        return ServiceResponse.ok(new byte[0]);
    }

    @Method(name = "Chat", clientStreaming = true, serverStreaming = true)
    public ServiceResponseContract chat(ContainerContract container, RouteContract route) {
        ServiceCallContract call = container.getSingleton(ServiceCallContract.class);

        for (Object message : call.getMessages()) {
            call.send(message);
        }

        return ServiceResponse.ok();
    }
}
```

| Annotation    | On       | States                                           |
| :------------ | :------- | :----------------------------------------------- |
| `@Service`    | A class  | The full service name, such as `example.Greeter` |
| `@Method`     | A method | The RPC name, and which side streams             |
| `@Middleware` | A method | One middleware class of the route                |

`@Middleware` is repeatable.

`io.valkyrja.grpc.routing.collector.AttributeRouteCollector` reads each class
that carries `@Service`, and each method of that class that carries `@Method`.
It builds the key from the two names, and it wires the method as the handler.

Warning: the collector constructs the controller with its no-argument
constructor, and it calls the method through reflection. The method takes a
`ContainerContract` and a `RouteContract`, and it returns a
`ServiceResponseContract`.

A route provider returns the controller classes and any prebuilt route.
`io.valkyrja.grpc.routing.provider.contract.GrpcRouteProviderContract` declares
the two methods, and a component provider returns its route providers from
`getGrpcProviders`. The [application component](../application/README.md)
describes the providers.

## The call

`io.valkyrja.grpc.message.call.contract.ServiceCallContract` holds the inbound
side of one call.

| Method              | Returns                                                |
| :------------------ | :----------------------------------------------------- |
| `getMethod()`       | The full method, which is the key of the service map   |
| `getMetadata()`     | The inbound metadata, which is the request headers     |
| `getDeadline()`     | The deadline, which is never null                      |
| `getCancellation()` | The cancellation token, which is never null            |
| `getPeer()`         | The peer of the connection                             |
| `getMessages()`     | The inbound messages                                   |
| `isStreaming()`     | Whether the call runs under the streaming model        |
| `send(message)`     | Pushes one outbound message, under the streaming model |

## The two models

The component dispatches a call under one of two models.

**The buffered model.** The adapter reads every inbound message, and it calls
the handler after the client half-closes. `getMessages()` returns the fixed
list, and the handler returns its messages on the response. A unary call, a
server-streaming call, and a client-streaming call each use this model.

**The streaming model.** The adapter calls the handler at once. `getMessages()`
returns a live stream, and each iteration blocks until the next message arrives.
The handler pushes each outbound message through `send`. A bidirectional call
uses this model.

Warning: `send` throws `GrpcNonStreamingSendException` under the buffered model.
Return the messages on the response instead.

Warning: `send` throws `GrpcConcurrentSendException` for a second call that
starts while the first one runs. The transport is not thread-safe, so a
streaming handler emits from one thread.

Warning: under the streaming model the inbound stream ends on a half-close, and
it ends on a cancellation as well. Read `getCancellation()` after the loop to
tell the two apart.

## Cancellation

Cancellation is cooperative. The framework never interrupts a handler, and a
handler that ignores the token runs to its end.

`CancellationTokenContract` holds the state.

```java
if (call.getCancellation().isCancelled()) {
    return ServiceResponse.cancelled(call.getCancellation().getReason());
}
```

`throwIfCancelled()` throws a `CancelledException` instead, and
`onCancelled(listener)` runs a listener when the call cancels. A token that is
cancelled already runs the listener at once.

`io.valkyrja.grpc.message.enum_.CancellationReason` holds two cases:
`CLIENT_CANCELLED` and `DEADLINE_EXCEEDED`. The component unifies the two
causes, so code tests the cancellation, and it reads the reason when the
difference matters.

`io.valkyrja.grpc.support.Cancellation.checkAndFinalize(call, response)` runs
the check at each boundary. It asks two questions. Has the call cancelled? Does
the response in hand carry a cancellation status? It returns a response to exit
with, or `null` to continue.

`call.cancellable(source)` wraps an iterable, and the iteration stops when the
call cancels.

## The status and the response

`io.valkyrja.grpc.message.status.Status` holds a code and a message.
`io.valkyrja.grpc.message.enum_.StatusCode` holds the seventeen gRPC codes, from
`OK` at 0 to `UNAUTHENTICATED` at 16. `Status` holds a named constructor for
each one.

```java
ServiceResponseContract response = ServiceResponse.of(Status.notFound("No such user."));
```

`io.valkyrja.grpc.message.response.ServiceResponse` holds the outbound side: the
status, the initial metadata, the trailing metadata, and the messages. It is
immutable, and each `with` method returns a new response. Six named constructors
build the common cases: `of(status)`, `ok()`, `ok(message)`, `unimplemented()`,
`unimplemented(message)`, and `cancelled(reason)`.

Warning: the initial metadata locks when the first message reaches the wire. The
trailing metadata stays open until the handler returns.

## Metadata

`io.valkyrja.grpc.message.metadata.Metadata` holds the key and value pairs of a
call. `Metadata` lowercases each key, so a key is case insensitive. The
constructor and each accessor lowercase the key first.

Both rules read the lowercase form. A key holds a letter, a digit, `-`, `_`, or
`.`. A key that ends in `-bin` carries a `byte[]` value, and every other key
carries a `String`.

```java
new Metadata(Map.of("X-Trace-Id", List.of("abc"))); // Stored as x-trace-id
new Metadata(Map.of("Trace-BIN", List.of(bytes)));  // Binary, because -BIN lowercases to -bin
```

Warning: `Metadata` validates on construction. It throws
`MetadataInvalidKeyException` for a key that breaks the charset rule, and
`MetadataInvalidValueException` for a value whose type does not match the kind
of its key.

## Middleware

The component holds seven stages.

| Stage             | Contract                            | Runs                                          |
| :---------------- | :---------------------------------- | :-------------------------------------------- |
| `CallReceived`    | `CallReceivedMiddlewareContract`    | Before the router reads the call              |
| `RouteMatched`    | `RouteMatchedMiddlewareContract`    | After the service map matched a route         |
| `RouteNotMatched` | `RouteNotMatchedMiddlewareContract` | After the service map matched nothing         |
| `RouteDispatched` | `RouteDispatchedMiddlewareContract` | After the handler returned a response         |
| `ThrowableCaught` | `ThrowableCaughtMiddlewareContract` | After a throwable reached the service handler |
| `SendingResponse` | `SendingResponseMiddlewareContract` | Before the adapter writes the response        |
| `ResponseSent`    | `ResponseSentMiddlewareContract`    | After the adapter wrote the response          |

`CallReceived` returns a `CallReceivedResult`, which holds a call and a nullable
response. `RouteMatched` returns a `RouteMatchedResult`, which holds a route and
a nullable response. A result that holds a response stops the dispatch.

`GrpcConfigContract` holds one list for each stage, and a `@Middleware`
annotation adds a middleware to one route.

Warning: the handler appends each middleware, and it removes no duplicate.

Warning: the handler resolves a middleware through `container.get`, so every
middleware class needs a binding. The
[container component](../container/README.md) describes the rule.

Under the streaming model each stage runs once for the call. `SendingResponse`
runs at the stream open, against an OK response whose initial metadata becomes
the headers. `ResponseSent` runs at the close.

## The adapter

`io.valkyrja.grpc.server.adapter.contract.ServiceAdapterContract` declares two
methods.

```java
void start(ServiceHandlerContract handler);

void stop();
```

An adapter accepts a native call, builds a `ServiceCall`, hands it to the
service handler, and writes the response back through the API of its library.
The TLS settings, the thread pool, and the port binding live on the
implementation.

The framework runs each handler on its own virtual thread, off the thread that
the library used to deliver the call. Two guarantees follow. The delivery of a
call returns without a wait, and the library reaches the framework with a later
event for the same call while the handler runs.

`io.valkyrja.application.entry.grpc.GrpcBridge` implements the bridge for
grpc-java. It depends on `io.grpc` alone, so every grpc-java transport shares
it. `NettyGrpc`, `JettyGrpc`, and `TomcatGrpc` build on it, and each one
declares its own SDK as `compileOnly`. The
[application component](../application/README.md) describes the entry classes.

`GrpcConfigContract.maxInboundMessages` bounds the inbound side, and the default
is 1000. Under the buffered model the bridge rejects a call that exceeds the
bound with `RESOURCE_EXHAUSTED`. Under the streaming model the value bounds the
window of the flow control, and it rejects no call.

## Container bindings

| Binding key                      | Published by                    | Resolves to                                      |
| :------------------------------- | :------------------------------ | :----------------------------------------------- |
| `RouterContract`                 | `GrpcRoutingServiceProvider`    | `Router`                                         |
| `RouteCollectionContract`        | `GrpcRoutingServiceProvider`    | The service map, filled from the route providers |
| `RouteCollectorContract`         | `GrpcRoutingServiceProvider`    | `AttributeRouteCollector`                        |
| `CallReceivedHandlerContract`    | `GrpcMiddlewareServiceProvider` | `CallReceivedHandler`                            |
| `RouteMatchedHandlerContract`    | `GrpcMiddlewareServiceProvider` | `RouteMatchedHandler`                            |
| `RouteNotMatchedHandlerContract` | `GrpcMiddlewareServiceProvider` | `RouteNotMatchedHandler`                         |
| `RouteDispatchedHandlerContract` | `GrpcMiddlewareServiceProvider` | `RouteDispatchedHandler`                         |
| `ThrowableCaughtHandlerContract` | `GrpcMiddlewareServiceProvider` | `ThrowableCaughtHandler`                         |
| `SendingResponseHandlerContract` | `GrpcMiddlewareServiceProvider` | `SendingResponseHandler`                         |
| `ResponseSentHandlerContract`    | `GrpcMiddlewareServiceProvider` | `ResponseSentHandler`                            |
| `ServiceHandlerContract`         | `GrpcServerServiceProvider`     | `ServiceHandler`                                 |

Warning: no provider binds `ServiceAdapterContract`. An adapter starts from the
entry class of the runtime, and the entry class holds the handler.

## Exceptions

`GrpcThrowable` is the contract of the component, and each sub-component
contract extends it: `GrpcRoutingThrowable`, `GrpcMiddlewareThrowable`, and
`GrpcServerThrowable`.

| Exception                           | The component throws it when                                                                  |
| :---------------------------------- | :-------------------------------------------------------------------------------------------- |
| `CancelledException`                | `throwIfCancelled` runs on a cancelled token                                                  |
| `GrpcNonStreamingSendException`     | `send` runs on a buffered call                                                                |
| `GrpcConcurrentSendException`       | A second `send` starts while the first one runs                                               |
| `GrpcInvalidStatusCodeException`    | A number matches no gRPC status code                                                          |
| `MetadataInvalidKeyException`       | A metadata key breaks the naming rule                                                         |
| `MetadataInvalidValueException`     | A metadata value does not match the type of its key                                           |
| `GrpcRoutingInvalidMethodException` | The `Route(method, handler)` constructor reads a method that is not `/package.Service/Method` |

A route provider that returns a prebuilt route with a malformed method therefore
fails at boot, inside `GrpcRoutingServiceProvider.publishRouteCollection`.

`RouteCollection.get` throws the same exception for a method that the service
map does not hold. The router calls `has` first, so the router never reaches it.
The [throwable component](../throwable/README.md) describes the hierarchy.
