/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.cli.routing.dispatcher;

import io.valkyrja.cli.interaction.input.contract.InputContract;
import io.valkyrja.cli.interaction.output.contract.OutputContract;
import io.valkyrja.cli.interaction.output.factory.contract.OutputFactoryContract;
import io.valkyrja.cli.middleware.handler.contract.ExitedHandlerContract;
import io.valkyrja.cli.middleware.handler.contract.RouteDispatchedHandlerContract;
import io.valkyrja.cli.middleware.handler.contract.RouteMatchedHandlerContract;
import io.valkyrja.cli.middleware.handler.contract.RouteNotMatchedHandlerContract;
import io.valkyrja.cli.middleware.handler.contract.ThrowableCaughtHandlerContract;
import io.valkyrja.cli.routing.collection.contract.RouteCollectionContract;
import io.valkyrja.cli.routing.data.contract.RouteContract;
import io.valkyrja.cli.routing.dispatcher.contract.RouterContract;
import io.valkyrja.container.manager.contract.ContainerContract;

public class Router implements RouterContract {

    protected final ContainerContract container;
    protected final RouteCollectionContract collection;
    protected final OutputFactoryContract outputFactory;
    protected final ThrowableCaughtHandlerContract throwableCaughtHandler;
    protected final RouteMatchedHandlerContract routeMatchedHandler;
    protected final RouteNotMatchedHandlerContract routeNotMatchedHandler;
    protected final RouteDispatchedHandlerContract routeDispatchedHandler;
    protected final ExitedHandlerContract exitedHandler;

    public Router(
            ContainerContract container,
            RouteCollectionContract collection,
            OutputFactoryContract outputFactory,
            ThrowableCaughtHandlerContract throwableCaughtHandler,
            RouteMatchedHandlerContract routeMatchedHandler,
            RouteNotMatchedHandlerContract routeNotMatchedHandler,
            RouteDispatchedHandlerContract routeDispatchedHandler,
            ExitedHandlerContract exitedHandler) {
        this.container = container;
        this.collection = collection;
        this.outputFactory = outputFactory;
        this.throwableCaughtHandler = throwableCaughtHandler;
        this.routeMatchedHandler = routeMatchedHandler;
        this.routeNotMatchedHandler = routeNotMatchedHandler;
        this.routeDispatchedHandler = routeDispatchedHandler;
        this.exitedHandler = exitedHandler;
    }

    @Override
    public OutputContract dispatch(InputContract input) {
        String commandName = input.getCommandName();

        if (!collection.has(commandName)) {
            OutputContract notFoundOutput = outputFactory.createOutput();
            return routeNotMatchedHandler.routeNotMatched(input, notFoundOutput);
        }

        RouteContract route = collection.get(commandName);

        Object afterMatched = routeMatchedHandler.routeMatched(input, route);

        if (afterMatched instanceof OutputContract earlyOutput) {
            return earlyOutput;
        }

        RouteContract matchedRoute = (RouteContract) afterMatched;
        OutputContract output = matchedRoute.getHandler().apply(container, matchedRoute);

        return routeDispatchedHandler.routeDispatched(input, output, matchedRoute);
    }
}
