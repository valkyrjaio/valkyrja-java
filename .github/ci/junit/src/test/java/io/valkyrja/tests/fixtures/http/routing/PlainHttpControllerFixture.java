/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.fixtures.http.routing;

import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.http.message.response.EmptyResponse;
import io.valkyrja.http.message.response.contract.ResponseContract;
import io.valkyrja.http.routing.attribute.Route;
import io.valkyrja.http.routing.attribute.route.Middleware;
import io.valkyrja.http.routing.data.contract.RouteContract;
import io.valkyrja.tests.fixtures.http.middleware.RequestReceivedOnlyHttpMiddlewareFixture;

/** A controller with no class-level {@code @Path}/{@code @Name} and an unrelated middleware. */
public final class PlainHttpControllerFixture {

    @Route(path = "/plain", name = "plain")
    @Middleware(name = RequestReceivedOnlyHttpMiddlewareFixture.class)
    public ResponseContract plain(ContainerContract container, RouteContract route) {
        return new EmptyResponse();
    }
}
