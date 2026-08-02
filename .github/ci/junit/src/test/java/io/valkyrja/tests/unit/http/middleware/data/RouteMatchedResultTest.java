/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.http.middleware.data;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;

import io.valkyrja.http.message.response.EmptyResponse;
import io.valkyrja.http.middleware.data.RouteMatchedResult;
import io.valkyrja.http.routing.data.contract.RouteContract;
import org.junit.jupiter.api.Test;

/** Test the {@link RouteMatchedResult}. */
final class RouteMatchedResultTest {

    @Test
    void exposesRouteAndResponse() {
        var route = mock(RouteContract.class);
        var response = new EmptyResponse();

        var result = new RouteMatchedResult(route, response);

        assertSame(route, result.route());
        assertSame(response, result.response());
        assertNull(new RouteMatchedResult(route, null).response());
    }
}
