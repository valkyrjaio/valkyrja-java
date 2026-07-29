/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.tests.unit.cli.routing.dispatcher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.valkyrja.cli.interaction.argument.Argument;
import io.valkyrja.cli.interaction.enum_.OptionType;
import io.valkyrja.cli.interaction.input.Input;
import io.valkyrja.cli.interaction.option.Option;
import io.valkyrja.cli.interaction.output.EmptyOutput;
import io.valkyrja.cli.interaction.output.factory.contract.OutputFactoryContract;
import io.valkyrja.cli.middleware.handler.contract.ProcessExitingHandlerContract;
import io.valkyrja.cli.middleware.handler.contract.RouteDispatchedHandlerContract;
import io.valkyrja.cli.middleware.handler.contract.RouteMatchedHandlerContract;
import io.valkyrja.cli.middleware.handler.contract.RouteNotMatchedHandlerContract;
import io.valkyrja.cli.middleware.handler.contract.ThrowableCaughtHandlerContract;
import io.valkyrja.cli.routing.collection.RouteCollection;
import io.valkyrja.cli.routing.data.ArgumentParameter;
import io.valkyrja.cli.routing.data.OptionParameter;
import io.valkyrja.cli.routing.data.Route;
import io.valkyrja.cli.routing.data.contract.RouteContract;
import io.valkyrja.cli.routing.dispatcher.Router;
import io.valkyrja.cli.routing.enum_.ArgumentMode;
import io.valkyrja.cli.routing.enum_.ArgumentValueMode;
import io.valkyrja.cli.routing.enum_.OptionMode;
import io.valkyrja.cli.routing.enum_.OptionValueMode;
import io.valkyrja.cli.routing.throwable.exception.CliRoutingArgumentValuesValidationException;
import io.valkyrja.cli.routing.throwable.exception.CliRoutingInvalidOptionWithValueException;
import io.valkyrja.cli.routing.throwable.exception.CliRoutingOptionValuesValidationException;
import io.valkyrja.container.manager.Container;
import io.valkyrja.tests.fixtures.cli.middleware.PassThroughMiddleware;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Test the cli {@link Router}. */
final class RouterTest {

    private Container container;
    private RouteCollection collection;
    private OutputFactoryContract outputFactory;
    private RouteMatchedHandlerContract routeMatchedHandler;
    private RouteNotMatchedHandlerContract routeNotMatchedHandler;
    private RouteDispatchedHandlerContract routeDispatchedHandler;
    private ThrowableCaughtHandlerContract throwableCaughtHandler;
    private ProcessExitingHandlerContract processExitingHandler;
    private Router router;

    @BeforeEach
    void setUp() {
        container = new Container();
        collection = new RouteCollection();
        outputFactory = mock(OutputFactoryContract.class);
        routeMatchedHandler = mock(RouteMatchedHandlerContract.class);
        routeNotMatchedHandler = mock(RouteNotMatchedHandlerContract.class);
        routeDispatchedHandler = mock(RouteDispatchedHandlerContract.class);
        throwableCaughtHandler = mock(ThrowableCaughtHandlerContract.class);
        processExitingHandler = mock(ProcessExitingHandlerContract.class);
        router =
                new Router(
                        container,
                        collection,
                        outputFactory,
                        throwableCaughtHandler,
                        routeMatchedHandler,
                        routeNotMatchedHandler,
                        routeDispatchedHandler,
                        processExitingHandler);
    }

    /** Stop dispatch after binding so the container holds the bound, validated route. */
    private void stopAfterBinding() {
        when(routeMatchedHandler.routeMatched(any(), any())).thenReturn(new EmptyOutput());
    }

    /** The route the container was given — bound and validated by dispatch. */
    private RouteContract boundRoute() {
        return container.get(RouteContract.class);
    }

    @Test
    void dispatchUnmatchedCommandUsesNotMatchedHandler() {
        var input = new Input().withCommandName("missing");
        var notFound = new EmptyOutput();
        var result = new EmptyOutput();
        when(outputFactory.createOutput()).thenReturn(notFound);
        when(routeNotMatchedHandler.routeNotMatched(input, notFound)).thenReturn(result);

        assertSame(result, router.dispatch(input));
    }

    @Test
    void dispatchReturnsEarlyOutputFromRouteMatchedHandler() {
        collection.add(new Route("list", "List", (c, r) -> new EmptyOutput()));
        var input = new Input().withCommandName("list");
        var early = new EmptyOutput();
        when(routeMatchedHandler.routeMatched(any(), any())).thenReturn(early);

        assertSame(early, router.dispatch(input));
    }

    @Test
    void dispatchRunsHandlerAndDispatchedMiddleware() {
        var matched = new Route("list", "List", (c, r) -> new EmptyOutput());
        collection.add(matched);
        var input = new Input().withCommandName("list");
        var dispatched = new EmptyOutput();
        when(routeMatchedHandler.routeMatched(any(), any())).thenReturn(matched);
        when(routeDispatchedHandler.routeDispatched(any(), any(), any())).thenReturn(dispatched);

        assertSame(dispatched, router.dispatch(input));
    }

    @Test
    void dispatchGivesHandlerAndContainerTheRouteAfterMiddleware() {
        var afterMiddleware = new Route("list", "List", (c, r) -> new EmptyOutput());
        collection.add(new Route("list", "List", (c, r) -> new EmptyOutput()));
        when(routeMatchedHandler.routeMatched(any(), any())).thenReturn(afterMiddleware);
        when(routeDispatchedHandler.routeDispatched(any(), any(), any()))
                .thenReturn(new EmptyOutput());

        router.dispatch(new Input().withCommandName("list"));

        assertSame(afterMiddleware, boundRoute());
    }

    @Test
    void dispatchRegistersRouteMiddlewareOnEachHandler() {
        collection.add(
                new Route("list", "List", (c, r) -> new EmptyOutput())
                        .withRouteMatchedMiddleware(List.of(PassThroughMiddleware.class))
                        .withRouteDispatchedMiddleware(List.of(PassThroughMiddleware.class))
                        .withThrowableCaughtMiddleware(List.of(PassThroughMiddleware.class))
                        .withProcessExitingMiddleware(List.of(PassThroughMiddleware.class)));
        stopAfterBinding();

        router.dispatch(new Input().withCommandName("list"));

        verify(routeMatchedHandler).add(PassThroughMiddleware.class);
        verify(routeDispatchedHandler).add(PassThroughMiddleware.class);
        verify(throwableCaughtHandler).add(PassThroughMiddleware.class);
        verify(processExitingHandler).add(PassThroughMiddleware.class);
    }

    // Argument binding.

    @Test
    void dispatchBindsSingleArgument() {
        var argument = new Argument("file.txt");
        collection.add(
                new Route("cp", "copy", (c, r) -> new EmptyOutput())
                        .withArguments(new ArgumentParameter("src", "Source")));
        stopAfterBinding();

        router.dispatch(new Input().withCommandName("cp").withArguments(argument));

        assertEquals(List.of(argument), boundRoute().getArgument("src").getArguments());
    }

    @Test
    void dispatchWithFewerArgumentsThanParameters() {
        var argument = new Argument("only");
        collection.add(
                new Route("mv", "move", (c, r) -> new EmptyOutput())
                        .withArguments(
                                new ArgumentParameter("first", "First argument"),
                                new ArgumentParameter("second", "Second argument")));
        stopAfterBinding();

        router.dispatch(new Input().withCommandName("mv").withArguments(argument));

        assertEquals(List.of(argument), boundRoute().getArgument("first").getArguments());
        assertEquals(List.of(), boundRoute().getArgument("second").getArguments());
    }

    @Test
    void dispatchLeavesOptionalArgumentEmptyWhenNoInputArguments() {
        collection.add(
                new Route("cp", "copy", (c, r) -> new EmptyOutput())
                        .withArguments(new ArgumentParameter("src", "Source")));
        stopAfterBinding();

        router.dispatch(new Input().withCommandName("cp"));

        assertEquals(List.of(), boundRoute().getArgument("src").getArguments());
    }

    @Test
    void dispatchArrayArgumentConsumesRemaining() {
        var first = new Argument("a");
        var restB = new Argument("b");
        var restC = new Argument("c");
        collection.add(
                new Route("rm", "remove", (c, r) -> new EmptyOutput())
                        .withArguments(
                                new ArgumentParameter("first", "First argument"),
                                new ArgumentParameter("rest", "Remaining arguments")
                                        .withValueMode(ArgumentValueMode.ARRAY)));
        stopAfterBinding();

        router.dispatch(new Input().withCommandName("rm").withArguments(first, restB, restC));

        assertEquals(List.of(first), boundRoute().getArgument("first").getArguments());
        assertEquals(List.of(restB, restC), boundRoute().getArgument("rest").getArguments());
    }

    @Test
    void dispatchThrowsWhenRequiredArgumentMissing() {
        collection.add(
                new Route("cp", "copy", (c, r) -> new EmptyOutput())
                        .withArguments(
                                new ArgumentParameter("required", "A required argument")
                                        .withMode(ArgumentMode.REQUIRED)));
        var input = new Input().withCommandName("cp");

        assertThrows(
                CliRoutingArgumentValuesValidationException.class, () -> router.dispatch(input));
    }

    // Option binding.

    @Test
    void dispatchBindsOptionByName() {
        var option = new Option("force", OptionType.LONG);
        collection.add(
                new Route("build", "Build", (c, r) -> new EmptyOutput())
                        .withOptions(new OptionParameter("force", "Force the command")));
        stopAfterBinding();

        router.dispatch(new Input().withCommandName("build").withOptions(option));

        assertEquals(List.of(option), boundRoute().getOption("force").getOptions());
    }

    @Test
    void dispatchBindsOptionByShortName() {
        var option = new Option("f", OptionType.SHORT);
        collection.add(
                new Route("build", "Build", (c, r) -> new EmptyOutput())
                        .withOptions(
                                new OptionParameter("force", "Force the command")
                                        .withShortNames("f")));
        stopAfterBinding();

        router.dispatch(new Input().withCommandName("build").withOptions(option));

        assertEquals(List.of(option), boundRoute().getOption("force").getOptions());
    }

    @Test
    void dispatchBindsMultipleOptionsToArrayOption() {
        var optionA = new Option("tag", "a", OptionType.LONG);
        var optionB = new Option("tag", "b", OptionType.LONG);
        collection.add(
                new Route("build", "Build", (c, r) -> new EmptyOutput())
                        .withOptions(
                                new OptionParameter("tag", "A repeatable tag")
                                        .withValueMode(OptionValueMode.ARRAY)));
        stopAfterBinding();

        router.dispatch(new Input().withCommandName("build").withOptions(optionA, optionB));

        assertEquals(List.of(optionA, optionB), boundRoute().getOption("tag").getOptions());
    }

    @Test
    void dispatchLeavesUnmatchedOptionParameterEmpty() {
        collection.add(
                new Route("build", "Build", (c, r) -> new EmptyOutput())
                        .withOptions(new OptionParameter("unused", "Never provided")));
        stopAfterBinding();

        router.dispatch(
                new Input()
                        .withCommandName("build")
                        .withOptions(new Option("other", OptionType.LONG)));

        assertEquals(List.of(), boundRoute().getOption("unused").getOptions());
    }

    @Test
    void dispatchThrowsWhenNoneFlagReceivesValue() {
        collection.add(
                new Route("build", "Build", (c, r) -> new EmptyOutput())
                        .withOptions(
                                new OptionParameter("flag", "A valueless flag")
                                        .withValueMode(OptionValueMode.NONE)));
        var input =
                new Input()
                        .withCommandName("build")
                        .withOptions(new Option("flag", "nope", OptionType.LONG));

        assertThrows(CliRoutingInvalidOptionWithValueException.class, () -> router.dispatch(input));
    }

    @Test
    void dispatchThrowsWhenRequiredOptionMissing() {
        collection.add(
                new Route("build", "Build", (c, r) -> new EmptyOutput())
                        .withOptions(
                                new OptionParameter("required", "A required option")
                                        .withMode(OptionMode.REQUIRED)));
        var input = new Input().withCommandName("build");

        assertThrows(CliRoutingOptionValuesValidationException.class, () -> router.dispatch(input));
    }

    @Test
    void dispatchThrowsWhenDefaultOptionReceivesMultiple() {
        collection.add(
                new Route("build", "Build", (c, r) -> new EmptyOutput())
                        .withOptions(
                                new OptionParameter("single", "A single-value option")
                                        .withValueMode(OptionValueMode.DEFAULT)));
        var input =
                new Input()
                        .withCommandName("build")
                        .withOptions(
                                new Option("single", "a", OptionType.LONG),
                                new Option("single", "b", OptionType.LONG));

        assertThrows(CliRoutingOptionValuesValidationException.class, () -> router.dispatch(input));
    }

    @Test
    void dispatchThrowsWhenOptionValueNotInValidValues() {
        collection.add(
                new Route("build", "Build", (c, r) -> new EmptyOutput())
                        .withOptions(
                                new OptionParameter("mode", "A constrained option")
                                        .withValidValues("dev", "prod")));
        var input =
                new Input()
                        .withCommandName("build")
                        .withOptions(new Option("mode", "staging", OptionType.LONG));

        assertThrows(CliRoutingOptionValuesValidationException.class, () -> router.dispatch(input));
    }

    @Test
    void dispatchBindsOptionValueWithinValidValues() {
        var option = new Option("mode", "prod", OptionType.LONG);
        collection.add(
                new Route("build", "Build", (c, r) -> new EmptyOutput())
                        .withOptions(
                                new OptionParameter("mode", "A constrained option")
                                        .withValidValues("dev", "prod")));
        stopAfterBinding();

        router.dispatch(new Input().withCommandName("build").withOptions(option));

        assertEquals(List.of(option), boundRoute().getOption("mode").getOptions());
    }
}
