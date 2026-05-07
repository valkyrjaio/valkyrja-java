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
import io.valkyrja.cli.middleware.handler.contract.RouteMatchedHandlerContract;
import io.valkyrja.cli.routing.data.contract.RouteContract;

public interface RouteMatchedMiddlewareContract {

    /** Returns either a RouteContract to dispatch, or OutputContract to short-circuit. */
    Object routeMatched(InputContract input, RouteContract route, RouteMatchedHandlerContract handler);
}
