/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.cli.middleware.handler;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;

import io.valkyrja.cli.interaction.input.contract.InputContract;
import io.valkyrja.cli.interaction.output.contract.OutputContract;
import io.valkyrja.cli.middleware.handler.ProcessExitingHandler;
import io.valkyrja.container.manager.Container;
import io.valkyrja.tests.fixtures.cli.middleware.PassThroughMiddlewareFixture;
import org.junit.jupiter.api.Test;

/** Test the {@link ProcessExitingHandler}. */
final class ProcessExitingHandlerTest {

    @Test
    void runsWithAndWithoutMiddleware() {
        var container = new Container();
        container.setSingleton(
                PassThroughMiddlewareFixture.class, new PassThroughMiddlewareFixture());

        assertDoesNotThrow(
                () ->
                        new ProcessExitingHandler(new Container())
                                .processExiting(
                                        mock(InputContract.class), mock(OutputContract.class)));
        assertDoesNotThrow(
                () ->
                        new ProcessExitingHandler(container, PassThroughMiddlewareFixture.class)
                                .processExiting(
                                        mock(InputContract.class), mock(OutputContract.class)));
    }
}
