# HTTP Routing & Middleware

## Introduction

The HTTP component turns a request into a response. It holds six sub-components.

| Sub-component | Holds                                                                                |
| :------------ | :----------------------------------------------------------------------------------- |
| `message`     | The request, the response, the URI, the headers, the stream, and the uploaded files  |
| `routing`     | The route, the collection, the collector, the processor, the matcher, and the router |
| `middleware`  | The seven middleware stages and their handlers                                       |
| `server`      | The request handler and the middleware the framework ships                           |
| `struct`      | The request struct and the response struct                                           |
| `client`      | The contract of an HTTP client                                                       |

`io.valkyrja.application.entry.Http` and the worker entry classes start the
component. The [application component](../application/README.md) describes them.

## The request handler

`io.valkyrja.http.server.handler.contract.RequestHandlerContract` runs one
request. `RequestHandler` is the default implementation, and `run` performs four
steps.

1. `handle(request)` binds the request as `ServerRequestContract`, runs the
   `RequestReceived` stage, and calls the router.
2. `sendingResponseHandler.sendingResponse(request, response)` runs the
   `SendingResponse` stage.
3. `send(response)` writes the status line, the headers, and the body.
4. `terminate(request, response)` runs the `ResponseSent` stage.

`handle` catches every `Throwable`.

Warning: the debug branch runs first, and it ends the request. When `debugMode`
is `true` the handler wraps the throwable in a `java.lang.RuntimeException` and
throws that out of `handle`. Nothing below this warning then happens: the
`ThrowableCaught` stage does not run, `run` never reaches `sendingResponse`,
`send`, or `terminate`, and no response reaches the client. A `catch` clause
that names the original type does not match the wrapper, so read the cause.
`HttpServerServiceProvider` reads `app.getDebugMode()` and passes it to the
constructor.

Warning: `HttpConfig` lists `LogThrowableCaughtMiddleware` by default, and debug
mode skips the stage that runs it. The one middleware the framework ships logs
nothing in the mode a developer runs.

Outside debug mode the handler builds a response from the throwable, and it runs
the `ThrowableCaught` stage with that response.

- A `HttpResponseException` that holds a response returns that response.
- A `HttpResponseException` that holds none returns a response with the status
  code of the exception.
- Every other throwable returns a 500 response.

## The router

`io.valkyrja.http.routing.dispatcher.Router` matches the request, and it
dispatches the route.

1. The router decodes the path of the request URI, and it asks the matcher for a
   route.
2. A path that matches no route of that request method, and that matches a route
   of `RequestMethod.ANY`, returns a 405 response. A path that matches nothing
   returns a 404 response. Each one runs the `RouteNotMatched` stage.
3. The router appends the middleware of the route to five stage handlers, and it
   binds the route as `RouteContract`.
4. The router runs the `RouteMatched` stage. A middleware that returns a
   response stops the dispatch.
5. The router binds the route that the stage returned, and it calls the handler
   of that route.
6. The router runs the `RouteDispatched` stage with the response of the handler.

The handler is a
`BiFunction<ContainerContract, RouteContract, ResponseContract>`, so a handler
resolves its own dependencies from the container.

## The matcher

`io.valkyrja.http.routing.matcher.Matcher` trims the slashes of the path, and it
matches in two steps. It looks the path up in the static index first. It then
reads each regex of the request method, and it returns the first route whose
regex matches the whole path.

A dynamic route reads its parameters from the named groups of the regex. A group
that the path does not fill takes the default of the parameter, and a parameter
with no default and no match keeps no value.

`Matcher.castMatchValue` applies the cast that the parameter declares. It asks
`getService()` for the type that `Cast.getType()` names, and it passes the
matched text under the key `CastArgument.VALUE`. It returns the converted value
when `isConvert()` is `true`, and the type itself when `isConvert()` is `false`.
The matcher holds the container, so no data object reaches it. The
[type component](../type/README.md) describes `Cast`.

Warning: `getService()` reads only a service binding, and it skips the singleton
cache. An alias, and an instance that `setSingleton` holds, raise
`ContainerInvalidReferenceException`, which escapes `match()`. A type that
`bindSingleton` registers is built for each match, and not once for the
application. Register a cast type with `bind`.

## The processor

`io.valkyrja.http.routing.processor.Processor` builds the regex of a dynamic
route. It replaces each `{name}` placeholder with a named capture group, and it
wraps the result in `^` and `$`.

```java
// The path      /users/{id}
// The parameter @Parameter(name = "id", regex = Regex.NUM)
// The regex     ^\/users\/(?<id>\d+)$
```

Warning: the regex is a native Java pattern, and it carries no delimiter.
`java.util.regex.Pattern` takes no delimiter, and it reads a slash as a literal
character.

`io.valkyrja.http.routing.constant.Regex` holds the regex of each parameter
type, such as `NUM`, `SLUG`, `ALPHA`, `UUID`, and `ULID`.

Warning: the `regex` member of `@Parameter` defaults to an empty string, and the
processor then writes an empty capture group. Name a regex for every parameter.

An optional parameter takes the slash before it into the group, so
`/users/{id?}` matches `/users` as well.

## Defining a route

A route is a method of a controller class. `io.valkyrja.http.routing.attribute`
holds `@Route`, `@DynamicRoute`, and `@Parameter`.
`io.valkyrja.http.routing.attribute.route` holds `@Path`, `@Name`,
`@Middleware`, `@RouteHandler`, `@RequestMethod`, `@RequestStruct`, and
`@ResponseStruct`.

```java
@Path("/api")
@Name("api")
public final class AppUserController {

    @Route(path = "/users", name = "index", requestMethods = {RequestMethod.GET})
    @Middleware(name = AppAuthMiddleware.class)
    public ResponseContract index(ContainerContract container, RouteContract route) {
        return new JsonResponse(
                Map.of("users", List.of()), StatusCode.OK, new HeaderCollection());
    }

    @DynamicRoute(
            path = "/users/{id}",
            name = "show",
            parameters = {@Parameter(name = "id", regex = Regex.NUM)})
    public ResponseContract show(ContainerContract container, RouteContract route) {
        DynamicRouteContract dynamic = (DynamicRouteContract) route;

        return new JsonResponse(
                Map.of("id", dynamic.getParameter("id").getValue()),
                StatusCode.OK,
                new HeaderCollection());
    }
}
```

| Annotation        | On                    | States                                                                   |
| :---------------- | :-------------------- | :----------------------------------------------------------------------- |
| `@Route`          | A method              | The path, the name, the request methods, the middleware, and the structs |
| `@DynamicRoute`   | A method              | The same, and the parameters of the path                                 |
| `@Parameter`      | A method, a parameter | One parameter of a dynamic route                                         |
| `@Path`           | A class, a method     | A prefix, and a suffix, for the path                                     |
| `@Name`           | A class, a method     | A prefix, and a suffix, for the name                                     |
| `@Middleware`     | A method              | One middleware class of the route                                        |
| `@RouteHandler`   | A method              | The class and the method that run the route                              |
| `@RequestMethod`  | A method              | The request methods of the route                                         |
| `@RequestStruct`  | A method              | The request struct of the route                                          |
| `@ResponseStruct` | A method              | The response struct of the route                                         |

`@Route`, `@DynamicRoute`, `@Middleware`, and `@Parameter` are repeatable.

`io.valkyrja.http.routing.attribute.route.requestmethod` holds one annotation
for each method: `@Get`, `@Post`, `@Put`, `@Patch`, `@Delete`, `@Head`,
`@Options`, `@Connect`, `@Trace`, and `@Any`. Each one is a `@RequestMethod`
annotation with its own method set.

`io.valkyrja.http.routing.collector.AttributeRouteCollector` reads the
annotations of each declared method. It joins the `@Path` of the class, the
`path` of the route, and the `@Path` of the method, and it joins the three name
parts with a period. A `@Route` whose path holds a `{` placeholder becomes a
dynamic route.

The request methods of a `@Route` default to `HEAD` and `GET`.

## Route providers

`io.valkyrja.http.routing.provider.contract.HttpRouteProviderContract` declares
two methods.

```java
List<Class<?>> getControllerClasses();

List<RouteContract> getRoutes();
```

`HttpRoutingServiceProvider.publishRouteCollection` reads every route provider
of the application. In debug mode it collects the routes from the annotations on
each boot. Otherwise it reads `HttpRoutingDataContract`, whose handlers are
direct method references, and no reflection runs.

Warning: `publishData` doubles as the publisher of `HttpRoutingDataContract`, so
an application that ships no generated routing data still resolves a full data
set. The [application component](../application/README.md) describes the
generated data.

## Middleware

The component holds seven stages.

| Stage             | Contract                            | Runs                                          |
| :---------------- | :---------------------------------- | :-------------------------------------------- |
| `RequestReceived` | `RequestReceivedMiddlewareContract` | Before the router reads the request           |
| `RouteMatched`    | `RouteMatchedMiddlewareContract`    | After the matcher matched a route             |
| `RouteNotMatched` | `RouteNotMatchedMiddlewareContract` | After the matcher matched no route            |
| `RouteDispatched` | `RouteDispatchedMiddlewareContract` | After the handler returned a response         |
| `ThrowableCaught` | `ThrowableCaughtMiddlewareContract` | After a throwable reached the request handler |
| `SendingResponse` | `SendingResponseMiddlewareContract` | Before the handler sends the response         |
| `ResponseSent`    | `ResponseSentMiddlewareContract`    | After the handler sent the response           |

`RequestReceived` returns a `RequestReceivedResult`, which holds a request and a
nullable response. `RouteMatched` returns a `RouteMatchedResult`, which holds a
route and a nullable response. A result that holds a response stops the
dispatch. Each other stage returns a response, and `ResponseSent` returns
nothing.

```java
public final class AppTimerMiddleware implements RouteDispatchedMiddlewareContract {

    @Override
    public ResponseContract routeDispatched(
            ServerRequestContract request,
            ResponseContract response,
            RouteContract route,
            RouteDispatchedHandlerContract handler) {
        return handler.routeDispatched(request, response, route);
    }
}
```

A middleware calls the handler to reach the next middleware of the stage. A
middleware that returns without the call stops the stage.

`HttpConfigContract` holds one list for each stage, and each list holds the
middleware of the whole application. A `@Middleware` annotation adds a
middleware to one route, and the router appends it when the route matches.

Warning: the handler appends each middleware, and it removes no duplicate. A
middleware that a config lists and an annotation names as well runs twice.

Warning: the handler resolves a middleware through `container.get`, so every
middleware class needs a binding. The
[container component](../container/README.md) describes the rule.

### The middleware the framework ships

| Class                             | Stage                             | Does                                                                        |
| :-------------------------------- | :-------------------------------- | :-------------------------------------------------------------------------- |
| `LogThrowableCaughtMiddleware`    | `ThrowableCaught`                 | Logs the throwable through `LoggerContract`                                 |
| `RequestStructMiddleware`         | `RouteMatched`                    | Validates the request against the struct of the route                       |
| `ResponseStructMiddleware`        | `RouteDispatched`                 | Builds the body of the response from the response struct of the route       |
| `RedirectTrailingSlashMiddleware` | `RequestReceived`                 | Returns a redirect for a path that ends in a slash                          |
| `NoCacheResponseMiddleware`       | `SendingResponse`                 | Adds the `Expires`, `Cache-Control`, and `Pragma` headers that stop a cache |
| `ViewRouteNotMatchedMiddleware`   | `RouteNotMatched`                 | Renders an error template into the body of the response                     |
| `CacheResponseMiddleware`         | `RequestReceived`, `ResponseSent` | Writes the response to a cache file                                         |

`HttpConfig` lists `LogThrowableCaughtMiddleware` in its
`throwableCaughtMiddleware`, and it lists no other middleware. An application
that wants one of the others lists it in its own config.

`HttpServerServiceProvider` publishes a binding for four of the seven:
`LogThrowableCaughtMiddleware`, `RequestStructMiddleware`,
`ResponseStructMiddleware`, and `CacheResponseMiddleware`.

Warning: `RedirectTrailingSlashMiddleware`, `NoCacheResponseMiddleware`, and
`ViewRouteNotMatchedMiddleware` carry no binding. A config that lists one of the
three throws `ContainerInvalidReferenceException` on the first request, so the
application publishes a binding for it as well. `ViewRouteNotMatchedMiddleware`
takes the renderer as its one constructor argument, and the application supplies
it.

Warning: `CacheResponseMiddleware` writes a cache file, and it deletes a file
that is older than its 1800-second lifetime. It returns no cached response, so a
cached file serves no request today.

## Structs

A struct states the shape of the data of one route.

`io.valkyrja.http.struct.request.contract.RequestStructContract` reads the
request. `values()` names the parameters that the struct takes, and
`getValidationRules` returns the rules of each one. `RequestStructMiddleware`
runs two checks for a route that holds a request struct.

- A request that carries a parameter the struct does not name returns a 413
  response.
- A request that fails the validation returns a 400 response.

`io.valkyrja.http.struct.response.contract.ResponseStructContract` builds the
data of the response. `asMap()` maps each key of the data to the key that the
response holds, and `getStructuredData(data, includeAll)` performs the mapping.

Three abstract request structs read one part of the request:
`QueryRequestStruct`, `ParsedBodyRequestStruct`, and `JsonRequestStruct`. The
[validation component](../validation/README.md) describes the rules, and the
[type component](../type/README.md) describes `ArrayableContract`.

## The message sub-component

| Package    | Holds                                                                                                              |
| :--------- | :----------------------------------------------------------------------------------------------------------------- |
| `request`  | `Request`, `ServerRequest`, and the factory that reads a native request                                            |
| `response` | `Response`, `TextResponse`, `HtmlResponse`, `JsonResponse`, `XmlResponse`, `RedirectResponse`, and `EmptyResponse` |
| `uri`      | `Uri`, its factory, and the scheme and port types                                                                  |
| `header`   | `Header`, the collection, and the typed headers                                                                    |
| `stream`   | `Stream`, its factory, and the seek and mode enums                                                                 |
| `file`     | `UploadedFile` and its collection                                                                                  |
| `param`    | The parameter collections, one for each part of a request                                                          |
| `enum_`    | `RequestMethod`, `StatusCode`, `StatusText`, `ProtocolVersion`, and `SameSite`                                     |

`MessageContract` holds the protocol version, the headers, and the body.
`RequestContract` adds the request target, the request method, and the URI.
`ServerRequestContract` adds six collections: the server params, the cookies,
the query, the uploaded files, the parsed body, and the attributes. Five of the
six live in the `param` package, and the uploaded file collection lives in the
`file` package. The `param` package also holds `ParsedJsonParamCollection`,
which `JsonServerRequestContract.getParsedJson` returns.

Each message is immutable. A `with` method returns a new message.

```java
ResponseContract response =
        new JsonResponse(Map.of("id", 1), StatusCode.OK, new HeaderCollection())
                .withStatusCode(StatusCode.CREATED);
```

`io.valkyrja.http.message.response.factory.contract.ResponseFactoryContract`
declares five methods: `createResponse`, `createTextResponse`,
`createJsonResponse`, `createJsonpResponse`, and `createRedirectResponse`.

```java
ResponseContract response = responseFactory.createJsonResponse(
        Map.of("id", 1), StatusCode.CREATED, null);
```

Each method takes a nullable status code and a nullable header collection. The
factory uses an empty collection for a `null` collection, and `StatusCode.OK`
for a `null` status code.

Warning: `createRedirectResponse` uses `StatusCode.FOUND` instead, which is 302.
Name the status code to send a 301.

Warning: the factory holds no method for `HtmlResponse`, `XmlResponse`, and
`EmptyResponse`. Construct one of the three directly.

## The client

`io.valkyrja.http.client.manager.contract.ClientContract` declares one method.

```java
ResponseContract sendRequest(RequestContract request);
```

Warning: the port ships no implementation of the contract, and no provider binds
it. An application that sends a request implements the contract, and publishes
the implementation.

## Container bindings

| Binding key                      | Published by                    | Resolves to                                        |
| :------------------------------- | :------------------------------ | :------------------------------------------------- |
| `ResponseFactoryContract`        | `HttpMessageServiceProvider`    | `ResponseFactory`                                  |
| `RequestReceivedHandlerContract` | `HttpMiddlewareServiceProvider` | `RequestReceivedHandler`                           |
| `RouteMatchedHandlerContract`    | `HttpMiddlewareServiceProvider` | `RouteMatchedHandler`                              |
| `RouteNotMatchedHandlerContract` | `HttpMiddlewareServiceProvider` | `RouteNotMatchedHandler`                           |
| `RouteDispatchedHandlerContract` | `HttpMiddlewareServiceProvider` | `RouteDispatchedHandler`                           |
| `ThrowableCaughtHandlerContract` | `HttpMiddlewareServiceProvider` | `ThrowableCaughtHandler`                           |
| `SendingResponseHandlerContract` | `HttpMiddlewareServiceProvider` | `SendingResponseHandler`                           |
| `ResponseSentHandlerContract`    | `HttpMiddlewareServiceProvider` | `ResponseSentHandler`                              |
| `RouterContract`                 | `HttpRoutingServiceProvider`    | `Router`                                           |
| `RouteCollectionContract`        | `HttpRoutingServiceProvider`    | `RouteCollection`, filled from the route providers |
| `MatcherContract`                | `HttpRoutingServiceProvider`    | `Matcher`                                          |
| `UrlContract`                    | `HttpRoutingServiceProvider`    | `Url`                                              |
| `RouteCollectorContract`         | `HttpRoutingServiceProvider`    | `AttributeRouteCollector`                          |
| `ProcessorContract`              | `HttpRoutingServiceProvider`    | `Processor`                                        |
| `RoutingResponseFactoryContract` | `HttpRoutingServiceProvider`    | `RoutingResponseFactory`                           |
| `HttpRoutingDataContract`        | `HttpRoutingServiceProvider`    | The collected routing data                         |
| `RequestHandlerContract`         | `HttpServerServiceProvider`     | `RequestHandler`                                   |
| `LogThrowableCaughtMiddleware`   | `HttpServerServiceProvider`     | The middleware instance                            |
| `RequestStructMiddleware`        | `HttpServerServiceProvider`     | The middleware instance                            |
| `ResponseStructMiddleware`       | `HttpServerServiceProvider`     | The middleware instance                            |
| `CacheResponseMiddleware`        | `HttpServerServiceProvider`     | The middleware instance                            |
| `ListCommand`                    | `HttpRoutingCliServiceProvider` | The command that lists the routes                  |

The framework ships one default for each key, and an application replaces a
default with a publisher of its own. The
[container component](../container/README.md) describes a service provider, and
the [application component](../application/README.md) describes the order in
which the providers register.

## Exceptions

Each sub-component holds its own throwables, and the contracts nest two levels
deep.

Six contracts extend `HttpThrowable`: `HttpMessageThrowable`,
`HttpRoutingThrowable`, `HttpMiddlewareThrowable`, `HttpServerThrowable`,
`HttpStructThrowable`, and `HttpClientThrowable`.

The message sub-component nests its own six under `HttpMessageThrowable`:
`HttpRequestThrowable`, `HttpResponseThrowable`, `HttpUriThrowable`,
`HttpHeaderThrowable`, `HttpStreamThrowable`, and `UploadedFileThrowable`. A
`catch` clause that names `HttpMessageThrowable` therefore covers a URI failure,
a header failure, a stream failure, and an uploaded file failure as well.

Three exceptions carry a response, and the request handler reads them outside
debug mode.

| Exception                       | Carries                                          |
| :------------------------------ | :----------------------------------------------- |
| `HttpResponseException`         | A status code, headers, and a nullable response  |
| `HttpNotFoundResponseException` | The same, with `StatusCode.NOT_FOUND` as default |
| `HttpRedirectResponseException` | The same, for a redirect                         |

Throw one of the three from a route handler to end the request with a response.
Every other throwable reaches the `ThrowableCaught` stage as a 500 response.

Warning: neither sentence holds in debug mode. The debug branch runs before the
handler reads the type of the throwable, so it wraps every throwable and ends
the request. The request handler section above describes that branch.

The [throwable component](../throwable/README.md) describes the hierarchy.
