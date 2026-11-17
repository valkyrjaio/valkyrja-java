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

`handle` catches every `Throwable`.

Warning: the debug branch runs first, and it ends the call. When `debugMode` is
`true` the handler wraps the throwable in a `java.lang.RuntimeException` and
throws that out of `handle`, so the `ThrowableCaught` stage does not run and
`run` never reaches `sending`. A `catch` clause that names the original type
does not match the wrapper, so read the cause. `GrpcServerServiceProvider` reads
`app.getDebugMode()` and passes it to the constructor.

Outside debug mode a `CancelledException` returns a cancelled response, and
every other throwable returns a response with the `INTERNAL` status. The
`ThrowableCaught` stage then runs with that response.

## The router

`io.valkyrja.grpc.routing.dispatcher.Router` looks the method of the call up in
the service map. The service map is
`io.valkyrja.grpc.routing.collection.RouteCollection`, which the container
publishes as `RouteCollectionContract`. The key is the full method, which reads
`/package.Service/Method`, and the match is exact.

1. A method that the service map does not hold returns an `UNIMPLEMENTED`
   response, and the `RouteNotMatched` stage runs.
2. The router appends the middleware of the route to five stage handlers. It
   then builds a copy of the call that carries the route, and it binds that copy
   as `ServiceCallContract`.
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

A service is a class, and each RPC is a method of that class.
`io.valkyrja.grpc.routing.attribute` holds `@Service` and `@Route`.
`io.valkyrja.grpc.routing.attribute.route` holds `@Handler` and
`@Middleware`.

```java
@Service(service = "example.Greeter")
public final class AppGreeterController {

    @Route(name = "SayHello", requestType = HelloRequest.class, responseType = HelloReply.class)
    @Middleware(name = AppAuthMiddleware.class)
    public ServiceResponseContract sayHello(ContainerContract container, RouteContract route) {
        return ServiceResponse.ok(new byte[0]);
    }

    @Route(name = "Chat", clientStreaming = true, serverStreaming = true)
    public ServiceResponseContract chat(ContainerContract container, RouteContract route) {
        ServiceCallContract call = container.getSingleton(ServiceCallContract.class);

        for (Object message : call.getMessages()) {
            call.send(message);
        }

        return ServiceResponse.ok();
    }
}
```

| Annotation    | On       | States                                                 |
| :------------ | :------- | :----------------------------------------------------- |
| `@Service`    | A class  | The full service name, such as `example.Greeter`       |
| `@Route`      | A method | Every field of the route, which the members below hold |
| `@Handler`    | A method | The class and the method that run the route            |
| `@Middleware` | A method | One middleware class of the route                      |

`@Middleware` is repeatable.

`io.valkyrja.grpc.routing.collector.AttributeRouteCollector` reads each class
that carries `@Service`, and each method of that class that carries `@Route`. It
builds the key from the two names.

### The handler

A route that names no handler runs the annotated method itself, which is what
the example above does. The collector resolves the controller from the
container, so a controller with constructor dependencies reaches them. Bind the
controller in a service provider.

A route names a handler of its own in one of two ways, and the collector reads
the annotation ahead of the member:

```java
@Route(name = "SayHello")
@Handler(handlerClass = AppGrpcRouteProvider.class, handlerMethod = "sayHelloHandler")
public ServiceResponseContract sayHello(ContainerContract container, RouteContract route) {
    return ServiceResponse.ok(new byte[0]);
}

@Route(
        name = "Greet",
        handler =
                @Handler(
                        handlerClass = AppGrpcRouteProvider.class,
                        handlerMethod = "greetHandler"))
public ServiceResponseContract greet(ContainerContract container, RouteContract route) {
    return ServiceResponse.ok(new byte[0]);
}
```

Warning: the annotated method does not run when the route names a handler. The
handler runs in its place.

The two spellings name the same two values, because a Java annotation member
holds a constant and so holds no callable. Either spelling names the absent
type, `Void.class`, when the route names no handler, and the collector then runs
the annotated method.

Each one names a static method that takes a `ContainerContract` and a
`RouteContract` and returns a `ServiceResponseContract`. The annotated method
takes the same parameters and returns the same response.

The collector holds a handler to that signature when it collects the route, so a
handler of the wrong signature fails at bootstrap rather than on a call. A
cached route holds the handler to the same signature, because a cached route and
a collected route answer a call alike.

### The middleware and the message types

`@Route` holds one member for each middleware stage:
`routeMatchedMiddleware`, `routeDispatchedMiddleware`,
`throwableCaughtMiddleware`, `sendingResponseMiddleware` and
`responseSentMiddleware`. Each one names the middleware classes of that stage.

`@Middleware` adds to those members rather than replacing them, and it names no
stage. The stages come from the middleware class.

The `requestType` and `responseType` members name the generated protobuf message
types, and each one defaults to `Void.class`, which the collector reads as no
type.

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
| `getRoute()`        | The route of the call, or `null` before the router ran |
| `hasRoute()`        | Whether the call carries a route                       |
| `withRoute(route)`  | A copy of the call that carries the route              |

Warning: the router binds the copy, and not the call that
`ServiceHandler.dispatchRouter` bound. A `CallReceived` middleware that keeps a
reference reads a call with no route. Resolve `ServiceCallContract` from the
container to read the current one.

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

| Exception                            | The component throws it when                                                                                                                                                                          |
| :----------------------------------- | :---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `CancelledException`                 | `throwIfCancelled` runs on a cancelled token                                                                                                                                                          |
| `GrpcNonStreamingSendException`      | `send` runs on a buffered call                                                                                                                                                                        |
| `GrpcConcurrentSendException`        | A second `send` starts while the first one runs                                                                                                                                                       |
| `GrpcInvalidStatusCodeException`     | A number matches no gRPC status code                                                                                                                                                                  |
| `MetadataInvalidKeyException`        | A metadata key breaks the naming rule                                                                                                                                                                 |
| `MetadataInvalidValueException`      | A metadata value does not match the type of its key                                                                                                                                                   |
| `GrpcRoutingInvalidMethodException`  | The `Route(method, handler)` constructor reads a method that is not `/package.Service/Method`                                                                                                         |
| `GrpcRoutingInvalidHandlerException` | A route's handler does not exist, is not static, is not accessible, or does not take the handler's signature, whether a convenience attribute, the `handler` member, or the annotated method named it |

A route provider that returns a prebuilt route with a malformed method therefore
fails at boot, inside `GrpcRoutingServiceProvider.publishRouteCollection`.

`RouteCollection.get` throws the same exception for a method that the service
map does not hold. The router calls `has` first, so the router never reaches it.
The [throwable component](../throwable/README.md) describes the hierarchy.
