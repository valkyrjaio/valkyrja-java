/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.cli.routing.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.cli.interaction.message.Message;
import io.valkyrja.cli.interaction.message.contract.MessageContract;
import io.valkyrja.cli.interaction.output.EmptyOutput;
import io.valkyrja.cli.interaction.output.contract.OutputContract;
import io.valkyrja.cli.middleware.contract.ProcessExitingMiddlewareContract;
import io.valkyrja.cli.middleware.contract.RouteDispatchedMiddlewareContract;
import io.valkyrja.cli.middleware.contract.RouteMatchedMiddlewareContract;
import io.valkyrja.cli.middleware.contract.ThrowableCaughtMiddlewareContract;
import io.valkyrja.cli.routing.data.ArgumentParameter;
import io.valkyrja.cli.routing.data.OptionParameter;
import io.valkyrja.cli.routing.data.Route;
import io.valkyrja.cli.routing.data.contract.RouteContract;
import io.valkyrja.cli.routing.throwable.exception.CliRoutingInvalidArgumentNameException;
import io.valkyrja.cli.routing.throwable.exception.CliRoutingInvalidOptionNameException;
import io.valkyrja.cli.routing.throwable.exception.CliRoutingNoHelpTextException;
import io.valkyrja.container.manager.contract.ContainerContract;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;

/** Test the {@link Route}. */
final class RouteTest {

    private static final BiFunction<ContainerContract, RouteContract, OutputContract> HANDLER =
            (container, route) -> new EmptyOutput();

    private static Route route() {
        return new Route("list", "List commands", HANDLER);
    }

    @Test
    void baseAccessorsAndHandler() {
        var route = route();

        assertEquals("list", route.getName());
        assertEquals("List commands", route.getDescription());
        assertSame(HANDLER, route.getHandler());
        assertEquals("renamed", route.withName("renamed").getName());
        assertEquals("desc", route.withDescription("desc").getDescription());
        BiFunction<ContainerContract, RouteContract, OutputContract> other =
                (c, r) -> new EmptyOutput();
        assertSame(other, route.withHandler(other).getHandler());
    }

    @Test
    void helpText() {
        var route = route();

        assertFalse(route.hasHelpText());
        assertThrows(CliRoutingNoHelpTextException.class, route::getHelpText);

        Supplier<MessageContract> help = () -> new Message("help!");
        var withHelp = route.withHelpText(help);
        assertTrue(withHelp.hasHelpText());
        assertSame(help, withHelp.getHelpText());
        assertEquals("help!", withHelp.getHelpTextMessage().getText());
    }

    @Test
    void arguments() {
        var route =
                route().withArguments(new ArgumentParameter("src", "Source"))
                        .withAddedArguments(new ArgumentParameter("dst", "Destination"));

        assertTrue(route.hasArguments());
        assertEquals(2, route.getArguments().size());
        assertTrue(route.hasArgument("src"));
        assertEquals("src", route.getArgument("src").getName());
        assertThrows(
                CliRoutingInvalidArgumentNameException.class, () -> route.getArgument("missing"));
    }

    @Test
    void options() {
        var route =
                route().withOptions(new OptionParameter("verbose", "Verbose"))
                        .withAddedOptions(new OptionParameter("quiet", "Quiet"));

        assertTrue(route.hasOptions());
        assertEquals(2, route.getOptions().size());
        assertTrue(route.hasOption("verbose"));
        assertEquals("verbose", route.getOption("verbose").getName());
        assertThrows(CliRoutingInvalidOptionNameException.class, () -> route.getOption("missing"));
    }

    @Test
    void middlewareCollections() {
        List<Class<? extends RouteMatchedMiddlewareContract>> matched = List.of();
        List<Class<? extends RouteDispatchedMiddlewareContract>> dispatched = List.of();
        List<Class<? extends ThrowableCaughtMiddlewareContract>> caught = List.of();
        List<Class<? extends ProcessExitingMiddlewareContract>> processExiting = List.of();

        var route =
                route().withRouteMatchedMiddleware(matched)
                        .withAddedRouteMatchedMiddleware(matched)
                        .withRouteDispatchedMiddleware(dispatched)
                        .withAddedRouteDispatchedMiddleware(dispatched)
                        .withThrowableCaughtMiddleware(caught)
                        .withAddedThrowableCaughtMiddleware(caught)
                        .withProcessExitingMiddleware(processExiting)
                        .withAddedProcessExitingMiddleware(processExiting);

        assertTrue(route.getRouteMatchedMiddleware().isEmpty());
        assertTrue(route.getRouteDispatchedMiddleware().isEmpty());
        assertTrue(route.getThrowableCaughtMiddleware().isEmpty());
        assertTrue(route.getProcessExitingMiddleware().isEmpty());
    }
}
