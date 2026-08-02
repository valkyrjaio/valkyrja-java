/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.fixtures.cli.routing;

import io.valkyrja.cli.routing.attribute.Route;
import io.valkyrja.cli.routing.attribute.route.Middleware;
import io.valkyrja.tests.fixtures.cli.middleware.InputReceivedOnlyMiddlewareFixture;

/** A cli controller with no class-level {@code @Name} and an unrelated middleware. */
public final class PlainControllerFixture {

    @Route(name = "plain", description = "Plain command")
    @Middleware(name = InputReceivedOnlyMiddlewareFixture.class)
    public void plain() {}
}
