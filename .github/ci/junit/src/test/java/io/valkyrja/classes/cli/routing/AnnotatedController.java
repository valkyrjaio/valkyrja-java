/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.classes.cli.routing;

import io.valkyrja.classes.cli.middleware.PassThroughMiddleware;
import io.valkyrja.cli.routing.attribute.ArgumentParameter;
import io.valkyrja.cli.routing.attribute.OptionParameter;
import io.valkyrja.cli.routing.attribute.Route;
import io.valkyrja.cli.routing.attribute.route.Middleware;
import io.valkyrja.cli.routing.attribute.route.Name;
import io.valkyrja.cli.routing.attribute.route.RouteHandler;

/** Fully-annotated controller exercising every branch of the attribute route collector. */
@Name("ctrl")
public final class AnnotatedController {

    @Route(name = "run", description = "Run it")
    @Name("method")
    @Middleware(name = PassThroughMiddleware.class)
    @ArgumentParameter(name = "target", description = "Target")
    @OptionParameter(name = "verbose", description = "Verbose", shortNames = "v")
    @RouteHandler(handlerClass = FixtureRouteHandler.class, handlerMethod = "handle")
    public void withHandler() {}

    @Route(name = "plain", description = "Plain command")
    public void defaultHandler() {}

    @Route(name = "fail", description = "Failing handler")
    @RouteHandler(handlerClass = FixtureRouteHandler.class, handlerMethod = "missingMethod")
    public void failingHandler() {}
}