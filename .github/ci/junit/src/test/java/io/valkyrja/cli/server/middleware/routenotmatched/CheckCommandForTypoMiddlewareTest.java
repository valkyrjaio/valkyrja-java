/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.cli.server.middleware.routenotmatched;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.valkyrja.cli.interaction.message.Answer;
import io.valkyrja.cli.interaction.output.Output;
import io.valkyrja.cli.interaction.output.contract.OutputContract;
import io.valkyrja.cli.middleware.handler.contract.RouteNotMatchedHandlerContract;
import io.valkyrja.cli.routing.collection.RouteCollection;
import io.valkyrja.cli.routing.data.Route;
import io.valkyrja.cli.routing.dispatcher.contract.RouterContract;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test the {@link CheckCommandForTypoMiddleware}. Placed in the source package to exercise the
 * protected question-callback methods, which are not reachable through the public method alone.
 */
final class CheckCommandForTypoMiddlewareTest {

    private RouterContract router;
    private RouteCollection collection;
    private RouteNotMatchedHandlerContract handler;
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        router = mock(RouterContract.class);
        collection = new RouteCollection();
        handler = mock(RouteNotMatchedHandlerContract.class);
        when(handler.routeNotMatched(any(), any()))
                .thenAnswer(invocation -> invocation.getArgument(1));
        // Mute the question output written during askToRunSimilarCommands.
        System.setOut(new PrintStream(new ByteArrayOutputStream(), true, StandardCharsets.UTF_8));
    }

    @AfterEach
    void restoreStdout() {
        System.setOut(originalOut);
    }

    private static Route route(String name) {
        return new Route(name, name, (c, r) -> new Output());
    }

    private CheckCommandForTypoMiddleware middleware() {
        return new CheckCommandForTypoMiddleware(router, collection);
    }

    @Test
    void passesThroughWhenNoSimilarCommand() {
        collection.add(route("build"));
        var input = new io.valkyrja.cli.interaction.input.Input().withCommandName("zzzzzz");
        var output = new Output();

        var result = middleware().routeNotMatched(input, output, handler);

        assertSame(output, result);
    }

    @Test
    void offersSimilarCommandButDoesNotDispatchWithoutSelection() {
        collection.add(route("list"));
        var input = new io.valkyrja.cli.interaction.input.Input().withCommandName("lst");
        var output = new Output();

        // matchedRoute stays null (no QuestionWriter selects), so the original output flows
        // through.
        var result = middleware().routeNotMatched(input, output, handler);

        // The offered question carries the callback lambda; invoke it to exercise that path.
        var question =
                (io.valkyrja.cli.interaction.message.contract.QuestionContract)
                        result.getMessages().stream()
                                .filter(
                                        m ->
                                                m
                                                        instanceof
                                                        io.valkyrja.cli.interaction.message.contract
                                                                .QuestionContract)
                                .findFirst()
                                .orElseThrow();
        var ignored = question.getCallable().apply(new Output(), new Answer("list"));
    }

    @Test
    void emptyCommandNameYieldsNoSimilarMatches() {
        collection.add(route("build"));
        var input = new io.valkyrja.cli.interaction.input.Input().withCommandName("");
        var output = new Output();

        assertSame(output, middleware().routeNotMatched(input, output, handler));
    }

    @Test
    void dispatchesSelectedCommandWhenAnswerMatches() {
        var listRoute = route("list");
        collection.add(listRoute);
        var middleware = middleware();
        var dispatched = new Output();
        when(router.dispatch(any())).thenReturn(dispatched);

        // Simulate the question callback selecting the "list" command.
        middleware.questionCallback(new Output(), new Answer("list"), List.of(listRoute));

        var input = new io.valkyrja.cli.interaction.input.Input().withCommandName("lst");
        var result = middleware.routeNotMatched(input, new Output(), handler);

        assertSame(dispatched, result);
    }

    @Test
    void questionCallbackWithNoAnswerClearsMatchedRoute() {
        var listRoute = route("list");
        var middleware = middleware();

        OutputContract result =
                middleware.questionCallback(new Output(), new Answer("no"), List.of(listRoute));

        // Callback returns the output unchanged; no route is selected.
        assertSame(result.getClass(), Output.class);
    }

    @Test
    void emptyRegisteredCommandNameIsSkipped() {
        // A registered command with an empty name exercises the empty-string guard in similarText.
        collection.add(route(""));
        var input = new io.valkyrja.cli.interaction.input.Input().withCommandName("build");
        var output = new Output();

        assertSame(output, middleware().routeNotMatched(input, output, handler));
    }
}
