/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.cli.routing.dispatcher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.valkyrja.cli.interaction.argument.Argument;
import io.valkyrja.cli.interaction.input.Input;
import io.valkyrja.cli.interaction.output.EmptyOutput;
import io.valkyrja.cli.interaction.output.contract.OutputContract;
import io.valkyrja.cli.interaction.output.factory.contract.OutputFactoryContract;
import io.valkyrja.cli.middleware.handler.contract.ProcessExitingHandlerContract;
import io.valkyrja.cli.middleware.handler.contract.RouteDispatchedHandlerContract;
import io.valkyrja.cli.middleware.handler.contract.RouteMatchedHandlerContract;
import io.valkyrja.cli.middleware.handler.contract.RouteNotMatchedHandlerContract;
import io.valkyrja.cli.middleware.handler.contract.ThrowableCaughtHandlerContract;
import io.valkyrja.cli.routing.collection.RouteCollection;
import io.valkyrja.cli.routing.data.ArgumentParameter;
import io.valkyrja.cli.routing.data.Route;
import io.valkyrja.cli.routing.data.contract.RouteContract;
import io.valkyrja.cli.routing.dispatcher.Router;
import io.valkyrja.cli.routing.enum_.ArgumentValueMode;
import io.valkyrja.container.manager.Container;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Test the cli {@link Router}. */
final class RouterTest {

    private RouteCollection collection;
    private OutputFactoryContract outputFactory;
    private RouteMatchedHandlerContract routeMatchedHandler;
    private RouteNotMatchedHandlerContract routeNotMatchedHandler;
    private RouteDispatchedHandlerContract routeDispatchedHandler;
    private Router router;

    @BeforeEach
    void setUp() {
        collection = new RouteCollection();
        outputFactory = mock(OutputFactoryContract.class);
        routeMatchedHandler = mock(RouteMatchedHandlerContract.class);
        routeNotMatchedHandler = mock(RouteNotMatchedHandlerContract.class);
        routeDispatchedHandler = mock(RouteDispatchedHandlerContract.class);
        router =
                new Router(
                        new Container(),
                        collection,
                        outputFactory,
                        mock(ThrowableCaughtHandlerContract.class),
                        routeMatchedHandler,
                        routeNotMatchedHandler,
                        routeDispatchedHandler,
                        mock(ProcessExitingHandlerContract.class));
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
    void bindArgumentsBindsSingleArgument() {
        var route =
                new Route("cp", "copy", (c, r) -> new EmptyOutput())
                        .withArguments(new ArgumentParameter("src", "Source"));
        collection.add(route);
        var input = new Input().withCommandName("cp").withArguments(new Argument("file.txt"));
        // Capture the bound route passed to the matched handler.
        when(routeMatchedHandler.routeMatched(any(), any()))
                .thenAnswer(
                        invocation -> {
                            RouteContract bound = invocation.getArgument(1);
                            assertEquals("file.txt", bound.getArgument("src").getFirstValue());
                            return new EmptyOutput();
                        });

        router.dispatch(input);
    }

    @Test
    void bindArgumentsConsumesAllRemainingForArrayArgument() {
        var arrayArg =
                (ArgumentParameter)
                        new ArgumentParameter("files", "Files")
                                .withValueMode(ArgumentValueMode.ARRAY);
        collection.add(
                new Route("rm", "remove", (c, r) -> new EmptyOutput()).withArguments(arrayArg));
        var input =
                new Input()
                        .withCommandName("rm")
                        .withArguments(new Argument("a"), new Argument("b"), new Argument("c"));
        when(routeMatchedHandler.routeMatched(any(), any()))
                .thenAnswer(
                        invocation -> {
                            RouteContract bound = invocation.getArgument(1);
                            assertEquals(3, bound.getArgument("files").getArguments().size());
                            return new EmptyOutput();
                        });

        router.dispatch(input);
    }

    @Test
    void bindArgumentsReturnsRouteWhenNoInputArguments() {
        var route =
                new Route("cp", "copy", (c, r) -> new EmptyOutput())
                        .withArguments(new ArgumentParameter("src", "Source"));
        collection.add(route);
        var input = new Input().withCommandName("cp");
        // Schemas present but no input arguments → route returned unchanged.
        when(routeMatchedHandler.routeMatched(any(), any()))
                .thenAnswer(
                        invocation -> {
                            RouteContract bound = invocation.getArgument(1);
                            assertEquals(0, bound.getArgument("src").getArguments().size());
                            return new EmptyOutput();
                        });

        router.dispatch(input);
    }

    @Test
    void bindArgumentsBindsMultipleSchemasAndSkipsMissingInput() {
        var route =
                new Route("mv", "move", (c, r) -> new EmptyOutput())
                        .withArguments(
                                new ArgumentParameter("src", "Source"),
                                new ArgumentParameter("dst", "Dest"));
        collection.add(route);
        var input = new Input().withCommandName("mv").withArguments(new Argument("a"));
        // Two schemas, one input: first schema binds, the last (non-array) schema is left unbound.
        when(routeMatchedHandler.routeMatched(any(), any()))
                .thenAnswer(
                        invocation -> {
                            RouteContract bound = invocation.getArgument(1);
                            assertEquals("a", bound.getArgument("src").getFirstValue());
                            assertEquals(0, bound.getArgument("dst").getArguments().size());
                            return new EmptyOutput();
                        });

        router.dispatch(input);
    }

}
