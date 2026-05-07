/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.cli.middleware.contract;

import io.valkyrja.cli.interaction.input.contract.InputContract;
import io.valkyrja.cli.interaction.output.contract.OutputContract;
import io.valkyrja.cli.middleware.handler.contract.RouteDispatchedHandlerContract;
import io.valkyrja.cli.routing.data.contract.RouteContract;

public interface RouteDispatchedMiddlewareContract {

    OutputContract routeDispatched(
            InputContract input,
            OutputContract output,
            RouteContract route,
            RouteDispatchedHandlerContract handler);
}
