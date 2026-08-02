/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.http.server.middleware.routenotmatched;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import io.valkyrja.http.message.request.contract.ServerRequestContract;
import io.valkyrja.http.message.response.HtmlResponse;
import io.valkyrja.http.middleware.handler.contract.RouteNotMatchedHandlerContract;
import io.valkyrja.http.server.middleware.routenotmatched.ViewRouteNotMatchedMiddleware;
import org.junit.jupiter.api.Test;

/** Test the {@link ViewRouteNotMatchedMiddleware}. */
final class ViewRouteNotMatchedMiddlewareTest {

    @Test
    void rendersErrorTemplateIntoResponseBody() {
        var middleware = new ViewRouteNotMatchedMiddleware((name, variables) -> "rendered:" + name);

        var result =
                middleware.routeNotMatched(
                        mock(ServerRequestContract.class),
                        new HtmlResponse(),
                        mock(RouteNotMatchedHandlerContract.class));

        // The HtmlResponse default status is 200, so the error template is errors/200.
        assertEquals("rendered:errors/200", result.getBody().toString());
    }
}
