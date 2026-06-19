/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.classes.cli.middleware;

import io.valkyrja.cli.interaction.input.contract.InputContract;
import io.valkyrja.cli.interaction.output.contract.OutputContract;
import io.valkyrja.cli.middleware.contract.ExitedMiddlewareContract;
import io.valkyrja.cli.middleware.contract.InputReceivedMiddlewareContract;
import io.valkyrja.cli.middleware.contract.RouteDispatchedMiddlewareContract;
import io.valkyrja.cli.middleware.contract.RouteMatchedMiddlewareContract;
import io.valkyrja.cli.middleware.contract.RouteNotMatchedMiddlewareContract;
import io.valkyrja.cli.middleware.contract.ThrowableCaughtMiddlewareContract;
import io.valkyrja.cli.middleware.handler.contract.ExitedHandlerContract;
import io.valkyrja.cli.middleware.handler.contract.InputReceivedHandlerContract;
import io.valkyrja.cli.middleware.handler.contract.RouteDispatchedHandlerContract;
import io.valkyrja.cli.middleware.handler.contract.RouteMatchedHandlerContract;
import io.valkyrja.cli.middleware.handler.contract.RouteNotMatchedHandlerContract;
import io.valkyrja.cli.middleware.handler.contract.ThrowableCaughtHandlerContract;
import io.valkyrja.cli.routing.data.contract.RouteContract;

/** A middleware implementing every cli contract that simply delegates back to the next handler. */
public final class PassThroughMiddleware
        implements InputReceivedMiddlewareContract,
                RouteMatchedMiddlewareContract,
                RouteNotMatchedMiddlewareContract,
                RouteDispatchedMiddlewareContract,
                ThrowableCaughtMiddlewareContract,
                ExitedMiddlewareContract {

    @Override
    public Object inputReceived(InputContract input, InputReceivedHandlerContract handler) {
        return handler.inputReceived(input);
    }

    @Override
    public Object routeMatched(
            InputContract input, RouteContract route, RouteMatchedHandlerContract handler) {
        return handler.routeMatched(input, route);
    }

    @Override
    public OutputContract routeNotMatched(
            InputContract input, OutputContract output, RouteNotMatchedHandlerContract handler) {
        return handler.routeNotMatched(input, output);
    }

    @Override
    public OutputContract routeDispatched(
            InputContract input,
            OutputContract output,
            RouteContract route,
            RouteDispatchedHandlerContract handler) {
        return handler.routeDispatched(input, output, route);
    }

    @Override
    public OutputContract throwableCaught(
            InputContract input,
            OutputContract output,
            Throwable throwable,
            ThrowableCaughtHandlerContract handler) {
        return handler.throwableCaught(input, output, throwable);
    }

    @Override
    public void exited(InputContract input, OutputContract output, ExitedHandlerContract handler) {
        handler.exited(input, output);
    }
}
