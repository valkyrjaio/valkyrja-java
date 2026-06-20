/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.classes.cli.routing;

import io.valkyrja.classes.cli.middleware.InputReceivedOnlyMiddleware;
import io.valkyrja.cli.routing.attribute.Route;
import io.valkyrja.cli.routing.attribute.route.Middleware;

/** A cli controller with no class-level {@code @Name} and an unrelated middleware. */
public final class PlainController {

    @Route(name = "plain", description = "Plain command")
    @Middleware(name = InputReceivedOnlyMiddleware.class)
    public void plain() {}
}
