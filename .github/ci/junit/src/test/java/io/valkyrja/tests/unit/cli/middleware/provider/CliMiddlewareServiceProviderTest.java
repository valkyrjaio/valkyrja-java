/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.cli.middleware.provider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import io.valkyrja.application.data.contract.CliConfigContract;
import io.valkyrja.cli.middleware.handler.contract.InputReceivedHandlerContract;
import io.valkyrja.cli.middleware.handler.contract.ProcessExitingHandlerContract;
import io.valkyrja.cli.middleware.handler.contract.RouteDispatchedHandlerContract;
import io.valkyrja.cli.middleware.handler.contract.RouteMatchedHandlerContract;
import io.valkyrja.cli.middleware.handler.contract.RouteNotMatchedHandlerContract;
import io.valkyrja.cli.middleware.handler.contract.ThrowableCaughtHandlerContract;
import io.valkyrja.cli.middleware.provider.CliMiddlewareServiceProvider;
import io.valkyrja.container.manager.Container;
import org.junit.jupiter.api.Test;

/** Test the {@link CliMiddlewareServiceProvider}. */
final class CliMiddlewareServiceProviderTest {

    private Container containerWithConfig() {
        var container = new Container();
        container.setSingleton(CliConfigContract.class, mock(CliConfigContract.class));

        return container;
    }

    @Test
    void publishersExposesAllSixHandlers() {
        assertEquals(6, new CliMiddlewareServiceProvider().publishers().size());
    }

    @Test
    void publishMethodsBindEachHandler() {
        var container = containerWithConfig();

        CliMiddlewareServiceProvider.publishInputReceivedHandler(container);
        CliMiddlewareServiceProvider.publishThrowableCaughtHandler(container);
        CliMiddlewareServiceProvider.publishRouteMatchedHandler(container);
        CliMiddlewareServiceProvider.publishRouteNotMatchedHandler(container);
        CliMiddlewareServiceProvider.publishRouteDispatchedHandler(container);
        CliMiddlewareServiceProvider.publishProcessExitingHandler(container);

        assertNotNull(container.getSingleton(InputReceivedHandlerContract.class));
        assertNotNull(container.getSingleton(ThrowableCaughtHandlerContract.class));
        assertNotNull(container.getSingleton(RouteMatchedHandlerContract.class));
        assertNotNull(container.getSingleton(RouteNotMatchedHandlerContract.class));
        assertNotNull(container.getSingleton(RouteDispatchedHandlerContract.class));
        assertNotNull(container.getSingleton(ProcessExitingHandlerContract.class));
    }
}
