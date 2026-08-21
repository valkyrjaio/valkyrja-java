# CLI Routing & Commands

## Introduction

The CLI component runs a command from the arguments of the process. It holds four
sub-components.

| Sub-component | Holds                                                    |
| :------------ | :------------------------------------------------------- |
| `interaction` | The input, the output, the messages, and the formatters  |
| `routing`     | The route, the collection, the collector, and the router |
| `middleware`  | The six middleware stages and their handlers             |
| `server`      | The input handler, the built-in commands, and the exiter |

`io.valkyrja.application.entry.Cli` starts the component. The
[application component](../application/README.md) describes the entry classes and
the config.

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
- A single `-` is an argument, because it names standard input.

Warning: `OptionFactory` throws `CliInteractionInvalidEmptyValueException` for a
combined short option that carries a value, such as `-abc=value`.

`InputContract` holds the caller, the command name, the arguments, and the
options. Each `with` method returns a new input, so a middleware changes the
input without a write.

## Defining a command

A command is a method of a controller class. The annotations live in
`io.valkyrja.cli.routing.attribute`.

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
`OutputContract`. A command with no `@RouteHandler` returns a new `Output` that holds no message.

Warning: the collector reads the `@Middleware` annotation for the middleware of a
route. It does not read the four middleware members of the `@Route` annotation.

The collector reads the mode of each parameter from the annotation.

| Enum                | Cases                      |
| :------------------ | :------------------------- |
| `ArgumentMode`      | `REQUIRED`, `OPTIONAL`     |
| `ArgumentValueMode` | `DEFAULT`, `ARRAY`         |
| `OptionMode`        | `REQUIRED`, `OPTIONAL`     |
| `OptionValueMode`   | `NONE`, `DEFAULT`, `ARRAY` |

## Route providers

`io.valkyrja.cli.routing.provider.contract.CliRouteProviderContract` declares two
methods.

```java
List<Class<?>> getControllerClasses();

List<RouteContract> getRoutes();
```

The collector reads the annotations of each controller class. A route in
`getRoutes` needs no annotation, and a provider returns both.

A component provider returns its route providers from `getCliProviders`.
`CliRoutingServiceProvider.publishRouteCollection` reads every route provider of
the application, collects the routes, and adds them to the collection.

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
6. The router runs the `RouteMatched` stage, binds the route as
   `RouteContract`, and calls the handler of the route.
7. The router runs the `RouteDispatched` stage with the output of the handler.

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

`InputReceived` returns an `InputContract` to continue, or an `OutputContract` to
stop. `RouteMatched` returns a `RouteContract` to continue, or an
`OutputContract` to stop. Each of the other four returns the type of its stage.

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

Each method takes an `ExitCode` and the messages, and each one has a variant that
takes the messages alone. The variant uses `ExitCode.SUCCESS`.

Warning: `FileOutput.outputMessage` and `StreamOutput.outputMessage` hold no
implementation, so neither type writes a message today.

`writeMessages()` writes each message that the output holds, and it moves the
message from the unwritten list to the written list. `InputHandler.run` calls it
once, after the router returns.

Three flags control the write. `isSilent` stops every write. `isQuiet` stops the
write while the exit code is `ExitCode.SUCCESS`. `isInteractive` states whether a
question reads an answer from the terminal.
`io.valkyrja.cli.interaction.data.CliInteractionConfig` holds the defaults, which
are interactive, and neither quiet nor silent.

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
`SUCCESS` at `0` to `AUTO_EXIT` at `255`. `OutputContract.withExitCode` takes the
enum, and it takes an `int` as well.

`InputHandler.run` reads the code from the output, and
`io.valkyrja.cli.server.support.Exiter` ends the process with it.

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

Each middleware handler reads its list from `CliConfigContract`, so the config of
the application decides the middleware of each stage.

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
`ListCommand`, `ListBashCommand`, and `VersionCommand`. Each one takes a route
and returns an output from `run()`.

Warning: no route provider of the framework registers these four commands, so an
application that wants one declares a route for it.

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

Three `InputReceived` middleware read them.
`CheckForHelpOptionsMiddleware` and `CheckForVersionOptionsMiddleware` replace
the command name when the input holds the option.
`CheckGlobalInteractionOptionsMiddleware` reads the quiet, silent, and
no-interaction options.

Warning: `CliConfig` lists no middleware, so an application that wants these
three lists them in its own config.

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
