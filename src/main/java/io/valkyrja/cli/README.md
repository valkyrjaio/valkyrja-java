# CLI Routing & Commands

## Introduction

The CLI component runs a command from the arguments of the process. It holds
four sub-components.

| Sub-component | Holds                                                    |
| :------------ | :------------------------------------------------------- |
| `interaction` | The input, the output, the messages, and the formatters  |
| `routing`     | The route, the collection, the collector, and the router |
| `middleware`  | The six middleware stages and their handlers             |
| `server`      | The input handler, the built-in commands, and the exiter |

`io.valkyrja.application.entry.Cli` starts the component. The
[application component](../application/README.md) describes the entry classes
and the config.

```java
Cli.run(new CliConfig(), args);
```

## The input

`io.valkyrja.cli.interaction.input.factory.InputFactory` reads the arguments of
the process, and it returns an `InputContract`.

```java
InputContract input = InputFactory.fromGlobals(args, "valkyrja", "list");
```

The second argument is the caller, and the third is the command to run when the
arguments name none. `CliConfig` holds both, as `applicationName` and
`defaultCommandName`, and their defaults are `valkyrja` and `list`.

The factory reads each argument in order.

- The first argument is the command name, unless it starts with `-`.
- An argument that starts with `-` is an option.
- Every other argument is an argument of the command, in the order it appears.

```bash
valkyrja app:list --limit=10 -v target
```

`OptionFactory` parses one option.

- `--name` is a long option, and `-n` is a short option.
- `--name=value` and `-n=value` carry a value.
- `-abc` is three short options, and each one carries no value.
- `--` ends the options. Every argument after it is an argument of the command.
- A single `-` is not an option, because it names standard input. After the
  first slot it is an argument of the command.

Warning: the factory tests the lone `-` before it tests the position, so a `-`
in the first slot becomes the command name. `valkyrja - < input.txt` runs a
command named `-`, and the router reaches the `RouteNotMatched` stage.

Warning: `OptionFactory` throws `CliInteractionInvalidEmptyValueException` for a
combined short option that carries a value, such as `-abc=value`.

`InputContract` holds the caller, the command name, the arguments, and the
options. Each `with` method returns a new input, so a middleware changes the
input without a write.

## Defining a command

A command is a method of a controller class. `io.valkyrja.cli.routing.attribute`
holds `@Route`, `@ArgumentParameter`, and `@OptionParameter`.
`io.valkyrja.cli.routing.attribute.route` holds `@Name`, `@Middleware`, and
`@RouteHandler`.

```java
@Name("app")
public final class AppListController {

    @Route(name = "list", description = "List the records")
    @Name("list")
    @Middleware(name = AppAuthMiddleware.class)
    @ArgumentParameter(name = "target", description = "The record type")
    @OptionParameter(name = "limit", description = "The record count", shortNames = "l")
    @RouteHandler(handlerClass = AppListHandler.class, handlerMethod = "handle")
    public void list() {}
}
```

| Annotation           | On                    | States                                              |
| :------------------- | :-------------------- | :-------------------------------------------------- |
| `@Route`             | A method              | The name and the description of the command         |
| `@Name`              | A class, a method     | A prefix, and a suffix, for the name of the command |
| `@RouteHandler`      | A method              | The class and the method that run the command       |
| `@Middleware`        | A method              | One middleware class of the command                 |
| `@ArgumentParameter` | A method, a parameter | One argument of the command                         |
| `@OptionParameter`   | A method, a parameter | One option of the command                           |

`@Route`, `@Middleware`, `@ArgumentParameter`, and `@OptionParameter` are
repeatable.

`io.valkyrja.cli.routing.collector.AttributeRouteCollector` reads the
annotations. It builds the name from the `@Name` of the class, the `name` of the
`@Route`, and the `@Name` of the method, and it joins the three with a period.
The example above names the command `app.list.list`.

`@RouteHandler` names a class with a no-argument constructor, and a method that
takes a `ContainerContract` and a `RouteContract` and returns an
`OutputContract`. A command with no `@RouteHandler` returns a new `Output` that
holds no message.

Warning: the collector reads the `@Middleware` annotation for the middleware of
a route. It does not read the four middleware members of the `@Route`
annotation.

The collector reads the mode of each parameter from the annotation.

| Enum                | Cases                      |
| :------------------ | :------------------------- |
| `ArgumentMode`      | `REQUIRED`, `OPTIONAL`     |
| `ArgumentValueMode` | `DEFAULT`, `ARRAY`         |
| `OptionMode`        | `REQUIRED`, `OPTIONAL`     |
| `OptionValueMode`   | `NONE`, `DEFAULT`, `ARRAY` |

## Route providers

`io.valkyrja.cli.routing.provider.contract.CliRouteProviderContract` declares
two methods.

```java
List<Class<?>> getControllerClasses();

List<RouteContract> getRoutes();
```

The collector reads the annotations of each controller class. A route in
`getRoutes` needs no annotation, and a provider returns both.

A component provider returns its route providers from `getCliProviders`.
`CliRoutingServiceProvider.publishRouteCollection` reads every route provider of
the application, and it adds the routes of `getRoutes` to the collection.

Warning: the framework publishes no binding for
`io.valkyrja.cli.routing.collector.contract.RouteCollectorContract`.
`publishRouteCollection` runs the collector only when
`container.isSingleton(RouteCollectorContract.class)` is `true`, so
`getControllerClasses` contributes nothing until an application publishes the
binding. Every annotation above this section then reaches no collection, and the
command it declares does not run.

Warning: a service provider does not close the gap. `isSingleton` reads no
callback, and `publishRouteCollection` runs on the first resolution of
`RouteCollectionContract`, so a deferred publisher for the collector leaves the
guard `false`. The application calls `setSingleton` for the collector before
anything resolves the collection.

```java
container.setSingleton(RouteCollectorContract.class, new AttributeRouteCollector());
```

The two sibling components differ. `HttpRoutingServiceProvider` publishes its
own collector and reads it with no guard. `GrpcRoutingServiceProvider` publishes
its own collector and guards on `container.has`, which does read the callbacks.

## The router

`io.valkyrja.cli.routing.dispatcher.Router` matches the command name against the
collection.

1. The router reads `input.getCommandName()`, and it looks the name up in the
   collection. The match is exact.
2. A name that the collection does not hold runs the `RouteNotMatched` stage.
3. The router binds each argument of the input to an argument of the route, in
   order of appearance. An argument whose value mode is `ARRAY` takes every
   remaining argument, so an array argument is last.
4. The router binds each option of the input to an option of the route. It
   matches the long name, and it matches each short name.
5. Each bound parameter runs `validateValues`.
6. The router appends the middleware of the route to the `RouteMatched`,
   `RouteDispatched`, `ThrowableCaught`, and `ProcessExiting` handlers, and it
   binds the route as `RouteContract`.
7. The router runs the `RouteMatched` stage. A middleware that returns an output
   stops the dispatch.
8. The router binds the route that the stage returned, and it calls the handler
   of that route.
9. The router runs the `RouteDispatched` stage with the output of the handler.

The handler is a `BiFunction<ContainerContract, RouteContract, OutputContract>`,
so a handler resolves its own dependencies from the container.

## Middleware

The component holds six stages. A stage has a contract, and a handler that runs
the middleware of that stage in order.

| Stage             | Contract                            | Runs                                        |
| :---------------- | :---------------------------------- | :------------------------------------------ |
| `InputReceived`   | `InputReceivedMiddlewareContract`   | Before the router reads the input           |
| `RouteMatched`    | `RouteMatchedMiddlewareContract`    | After the collection matched the command    |
| `RouteNotMatched` | `RouteNotMatchedMiddlewareContract` | After the collection matched no command     |
| `RouteDispatched` | `RouteDispatchedMiddlewareContract` | After the handler returned an output        |
| `ThrowableCaught` | `ThrowableCaughtMiddlewareContract` | After a throwable reached the input handler |
| `ProcessExiting`  | `ProcessExitingMiddlewareContract`  | Before the process exits                    |

`InputReceived` returns an `InputContract` to continue, or an `OutputContract`
to stop. `RouteMatched` returns a `RouteContract` to continue, or an
`OutputContract` to stop. `RouteNotMatched`, `RouteDispatched`, and
`ThrowableCaught` each return an `OutputContract`, and `ProcessExiting` returns
nothing.

Warning: a `ProcessExiting` middleware returns `void`, so it cannot change the
output. The exit code that `InputHandler.run` reads is already fixed when the
stage runs.

The `ProcessExiting` stage runs under a guard of its own. A middleware that
throws there makes `run` print a first report, which a subclass can point at any
destination. A raise while `run` builds or writes that report makes it print the
report that answers it. The code the command set still reaches `Exiter.exit`.

A first report comes from `getOutputFromThrowable`, which a subclass overrides
and can point at any destination. `InputHandler` builds a direct report itself,
so no override and no flag reaches it. A direct report names the command, and it
names none when reading the command name from the input is itself what raised.
That one report carries a `Report message:` line, which names the raise that
removed the command. Every report reads a throwable's message through one call,
which stands in the text `the throwable reports no message` when the throwable
carries none and when reading it raises.

Warning: the `ThrowableCaught` stage receives a `null` output from
`InputHandler.handle`. That call passes the result of `emptyOutput()`, which is
`null`, so a middleware of that stage builds its own output. A middleware that
reads the output it receives throws a `NullPointerException` on the first
throwable that `handle` catches.

Warning: the `ThrowableCaught` stage needs a middleware. The handler ends in
`Objects.requireNonNull` for the output, and `CliConfig` lists no middleware at
that stage. A command that throws therefore makes the handler raise a
`NullPointerException`, which `handle` catches and reports as the recovery
throwable.

`InputHandler.run` runs the stage again when the output write throws, and that
run passes a non-null output. A middleware of this stage therefore receives
`CliInteractionFileWriteException` and `CliInteractionStreamWriteException` as
well as a throwable a command raised.

A middleware of this stage can itself throw. `handle` and `run` each build an
output then, which names the throwable it caught and the middleware's. `handle`
returns that output, and `run` writes it.

Warning: the run in `run` resumes the chain rather than restarting it.
`Handler` advances its index once for each middleware it resolves and never
rewinds it, and `CliMiddlewareServiceProvider` publishes one handler as a
singleton. A command that throws makes `handle` run the stage first, so a first
run that reached the end of the chain leaves no middleware for the one in
`run`. Issue #182 tracks it.

```java
public final class AppTimerMiddleware implements RouteDispatchedMiddlewareContract {

    @Override
    public OutputContract routeDispatched(
            InputContract input,
            OutputContract output,
            RouteContract route,
            RouteDispatchedHandlerContract handler) {
        return handler.routeDispatched(input, output, route);
    }
}
```

A middleware calls the handler to reach the next middleware of the stage. A
middleware that returns without the call stops the stage.

`CliConfigContract` holds one list for each stage, and each list holds the
middleware of the whole application. A `@Middleware` annotation adds a
middleware to one command, and the router appends it when the command matches.

Warning: the handler appends each middleware, and it removes no duplicate. A
middleware that a config lists and an annotation names as well runs twice.

Warning: the handler resolves a middleware through `container.get`, so every
middleware class needs a binding. The
[container component](../container/README.md) describes the rule.

## The output

`OutputContract` holds the messages, the writers, three interaction flags, and
the exit code. Each `with` method returns a new output.

`io.valkyrja.cli.interaction.output.factory.contract.OutputFactoryContract`
builds one of five output types.

| Method                            | Type           | Writes                                   |
| :-------------------------------- | :------------- | :--------------------------------------- |
| `createOutput(...)`               | `Output`       | The formatted text to `System.out`       |
| `createPlainOutput(...)`          | `PlainOutput`  | The text to `System.out`, with no format |
| `createEmptyOutput(...)`          | `EmptyOutput`  | Nothing                                  |
| `createFileOutput(filepath, ...)` | `FileOutput`   | A file                                   |
| `createStreamOutput(stream, ...)` | `StreamOutput` | A stream                                 |

Each method takes an `ExitCode` and the messages, and each one has a variant
that takes the messages alone. The variant uses `ExitCode.SUCCESS`.

`FileOutput` appends the formatted text to the filepath, and it makes the file
when the file does not exist. It makes no directory, so a filepath under a
directory that does not exist fails the write. `StreamOutput` writes the
formatted text to the stream and flushes it. A failed write throws
`CliInteractionFileWriteException` or `CliInteractionStreamWriteException`, each
wrapping the `IOException` as its cause.

`FileOutput` never truncates. The file keeps the messages of each earlier run,
and the caller owns truncation.

`writeMessages()` writes each message that the output holds. `InputHandler.run`
calls it after `handle` returns, again for each of the two outputs that a failed
write recovers to, again for each of the two reports of a throwable the exit
stage raised, and again for the report of an exit code the run could not use.

Warning: `writeMessages()` returns a new output, and the receiver keeps its
unwritten list. Keep the return value when the moved state matters.
`InputHandler.run` keeps it for the write of the command's own output, and for
the write of each of the two outputs that a failed write recovers to. It
registers the output it holds after those writes as the `OutputContract`
singleton. It discards the return of each exit-stage report and of the exit code
report, which no later stage reads.

Two flags control the write. `isSilent` stops it. `isQuiet` stops it while the
exit code is `ExitCode.SUCCESS`. A third flag, `isInteractive`, reaches no write
path. It states whether a question reads an answer from the terminal.
`io.valkyrja.cli.interaction.data.CliInteractionConfig` holds the defaults,
which are interactive, and neither quiet nor silent.

Warning: a stopped write still records the message. `writeMessageInternal` adds
the message to the written list before it reads either flag, so
`getWrittenMessages()` and `hasWrittenMessage()` report a message that no
terminal received.

Warning: `Output` writes each message directly, and it consults no writer. An
output holds a list of `WriterContract`, and a subclass that overrides
`writeMessageInternal` reads that list.

### Messages and formatters

A message is a `MessageContract`. The component ships `Message`, `NewLine`,
`Banner`, `Header`, `ErrorMessage`, `SuccessMessage`, `WarningMessage`,
`Question`, `Answer`, and `Progress`.

A formatter wraps the text in ANSI escape codes.
`io.valkyrja.cli.interaction.formatter.Formatter` takes one format or more, and
the component ships `ErrorFormatter`, `SuccessFormatter`, `WarningFormatter`,
`QuestionFormatter`, and `HighlightedTextFormatter`. A format is a
`TextColorFormat`, a `BackgroundColorFormat`, or a `StyleFormat`, and each one
reads its code from the `TextColor`, `BackgroundColor`, or `Style` enum.

```java
MessageContract message = new Message("Routes:", new HighlightedTextFormatter());
```

## The exit code

`io.valkyrja.cli.interaction.enum_.ExitCode` holds the `sysexits` codes, from
`SUCCESS` at `0` to `AUTO_EXIT` at `255`. `OutputContract.withExitCode` takes
the enum, and it takes an `int` as well.

`InputHandler.run` reads the code from the output, and
`io.valkyrja.cli.server.support.Exiter` ends the process with it. The read
carries a guard, so an output that raises on it, or holds a value that fits no
code, ends the process with `ERROR`. That guard prints a direct report, which
names the raise and names the command whenever the input reads.

## Container bindings

| Binding key                      | Published by                    | Resolves to                                                                                    |
| :------------------------------- | :------------------------------ | :--------------------------------------------------------------------------------------------- |
| `CliInteractionConfigContract`   | `CliInteractionServiceProvider` | The application config when it implements the contract, and a `CliInteractionConfig` otherwise |
| `OutputFactoryContract`          | `CliInteractionServiceProvider` | `OutputFactory`                                                                                |
| `InputReceivedHandlerContract`   | `CliMiddlewareServiceProvider`  | `InputReceivedHandler`                                                                         |
| `RouteMatchedHandlerContract`    | `CliMiddlewareServiceProvider`  | `RouteMatchedHandler`                                                                          |
| `RouteNotMatchedHandlerContract` | `CliMiddlewareServiceProvider`  | `RouteNotMatchedHandler`                                                                       |
| `RouteDispatchedHandlerContract` | `CliMiddlewareServiceProvider`  | `RouteDispatchedHandler`                                                                       |
| `ThrowableCaughtHandlerContract` | `CliMiddlewareServiceProvider`  | `ThrowableCaughtHandler`                                                                       |
| `ProcessExitingHandlerContract`  | `CliMiddlewareServiceProvider`  | `ProcessExitingHandler`                                                                        |
| `RouterContract`                 | `CliRoutingServiceProvider`     | `Router`                                                                                       |
| `RouteCollectionContract`        | `CliRoutingServiceProvider`     | `RouteCollection`, filled from the route providers                                             |
| `InputHandlerContract`           | `CliServerServiceProvider`      | `InputHandler`                                                                                 |

Each middleware handler reads its list from `CliConfigContract`, so the config
of the application decides the middleware of each stage.

The framework ships one default for each key, and an application replaces a
default with a publisher of its own. To run a different input handler, implement
`InputHandlerContract` and publish it.

```java
public class AppCliServiceProvider implements ServiceProviderContract {

    @Override
    public Map<Class<?>, Consumer<ContainerContract>> publishers() {
        return Map.of(InputHandlerContract.class, AppCliServiceProvider::publishInputHandler);
    }

    public static void publishInputHandler(ContainerContract container) {
        container.setSingleton(InputHandlerContract.class, new AppInputHandler());
    }
}
```

List the component provider of the application last, so its publisher replaces
the default. The [application component](../application/README.md) describes the
order.

## The built-in commands

The `server` sub-component ships four command classes: `HelpCommand`,
`ListCommand`, `ListBashCommand`, and `VersionCommand`. Each one extends the
abstract `io.valkyrja.cli.server.command.abstract_.Command`, which takes the
route, and each one returns an output from `run()`.

Warning: the four constructors differ, and none of them takes the route alone.
`HelpCommand` and `ListCommand` take the namespace of the application, its
version, the route, the collection, and the output factory. `ListBashCommand`
takes the route, the collection, and the output factory. `VersionCommand` takes
the output factory, the namespace, the version, and the route.

Warning: no route provider of the framework registers these four commands. An
annotation on one of them registers nothing either, because the collector that
reads an annotation carries no binding. An application that wants one returns a
prebuilt `RouteContract` from `getRoutes()`.

`io.valkyrja.cli.server.constant.CliCommandName` holds the three command names,
and `io.valkyrja.cli.routing.constant.OptionName` and `OptionShortName` hold the
six global option names.

| Option           | Short name |
| :--------------- | :--------- |
| `help`           | `h`        |
| `version`        | `v`        |
| `quiet`          | `q`        |
| `silent`         | `s`        |
| `no-interaction` | `N`        |
| `token`          | `t`        |

Three `InputReceived` middleware read them. `CheckForHelpOptionsMiddleware` and
`CheckForVersionOptionsMiddleware` replace the command name when the input holds
the option. `CheckGlobalInteractionOptionsMiddleware` reads the quiet, silent,
and no-interaction options. The component ships a fourth,
`CheckCommandForTypoMiddleware`, which runs at the `RouteNotMatched` stage.

Warning: `CliConfig` lists none of the four, and no provider binds them. An
application publishes a binding for the middleware as well as listing it in its
config. Without the binding the handler throws
`ContainerInvalidReferenceException` on the first run.

None of the four holds a no-argument constructor, so the binding supplies the
arguments.

| Middleware                                | Constructor takes                                                                |
| :---------------------------------------- | :------------------------------------------------------------------------------- |
| `CheckForHelpOptionsMiddleware`           | The command name, the option name, and the short name                            |
| `CheckForVersionOptionsMiddleware`        | The command name, the option name, and the short name                            |
| `CheckGlobalInteractionOptionsMiddleware` | The interaction config, and the name and short name of each of the three options |
| `CheckCommandForTypoMiddleware`           | The router, the route collection, and an optional default answer                 |

## Exceptions

Each sub-component holds its own throwables. `CliThrowable` is the contract of
the component, and each sub-component contract extends it:
`CliInteractionThrowable`, `CliMiddlewareThrowable`, `CliRoutingThrowable`, and
`CliServerThrowable`.

| Exception                                       | The component throws it when                                               |
| :---------------------------------------------- | :------------------------------------------------------------------------- |
| `CliInteractionInvalidOptionNameException`      | An argument that does not start with `-` reaches `OptionFactory`           |
| `CliInteractionInvalidNonEmptyValueException`   | An option holds an empty name                                              |
| `CliInteractionInvalidEmptyValueException`      | A combined short option carries a value                                    |
| `CliInteractionExpectedQuestionOutputException` | A message that is not a question reaches `QuestionWriter`                  |
| `CliInteractionNoFormatterException`            | `Message.getFormatter` runs, and the message holds none                    |
| `CliInteractionNoValidationCallableException`   | `Answer.getValidationCallable` runs, and the answer holds none             |
| `CliInteractionFileWriteException`              | `FileOutput.outputMessage` runs, and the write to the file fails           |
| `CliInteractionStreamWriteException`            | `StreamOutput.outputMessage` runs, and the write to the stream fails       |
| `CliRoutingInvalidRouteNameException`           | `RouteCollection.get` runs, and the collection holds no route of that name |
| `CliRoutingInvalidArgumentNameException`        | `Route.getArgument` runs, and the route holds no argument of that name     |
| `CliRoutingInvalidOptionNameException`          | `Route.getOption` runs, and the route holds no option of that name         |
| `CliRoutingInvalidOptionWithValueException`     | An option whose value mode is `NONE` carries a value                       |
| `CliRoutingArgumentValuesValidationException`   | An argument fails `validateValues`                                         |
| `CliRoutingOptionValuesValidationException`     | An option fails `validateValues`                                           |
| `CliRoutingNoHelpTextException`                 | `Route.getHelpText` runs, and the route holds none                         |

The component also ships `CliRoutingNoCastException`,
`CliRoutingInvalidHelpTextCallableException`, and
`CliRoutingNoOutputDispatchException`. No class throws them today.

The [throwable component](../throwable/README.md) describes the hierarchy.
