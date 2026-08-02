/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.fixtures.cli.routing;

import io.valkyrja.cli.routing.attribute.ArgumentParameter;
import io.valkyrja.cli.routing.attribute.OptionParameter;
import io.valkyrja.cli.routing.attribute.Route;
import io.valkyrja.cli.routing.attribute.route.Middleware;
import io.valkyrja.cli.routing.attribute.route.Name;
import io.valkyrja.cli.routing.attribute.route.RouteHandler;
import io.valkyrja.tests.fixtures.cli.middleware.PassThroughMiddlewareFixture;

/** Fully-annotated controller exercising every branch of the attribute route collector. */
@Name("ctrl")
public final class AnnotatedControllerFixture {

    @Route(name = "run", description = "Run it")
    @Name("method")
    @Middleware(name = PassThroughMiddlewareFixture.class)
    @ArgumentParameter(name = "target", description = "Target")
    @OptionParameter(name = "verbose", description = "Verbose", shortNames = "v")
    @RouteHandler(handlerClass = RouteHandlerFixture.class, handlerMethod = "handle")
    public void withHandler() {}

    @Route(name = "plain", description = "Plain command")
    public void defaultHandler() {}

    @Route(name = "fail", description = "Failing handler")
    @RouteHandler(handlerClass = RouteHandlerFixture.class, handlerMethod = "missingMethod")
    public void failingHandler() {}
}
