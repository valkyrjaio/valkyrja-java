/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.classes.cli.routing;

import io.valkyrja.cli.interaction.output.EmptyOutput;
import io.valkyrja.cli.interaction.output.contract.OutputContract;
import io.valkyrja.cli.routing.data.contract.RouteContract;
import io.valkyrja.container.manager.contract.ContainerContract;

/** A route handler resolved reflectively by {@code @RouteHandler} in attribute routing tests. */
public final class FixtureRouteHandler {

    public OutputContract handle(ContainerContract container, RouteContract route) {
        return new EmptyOutput();
    }
}
