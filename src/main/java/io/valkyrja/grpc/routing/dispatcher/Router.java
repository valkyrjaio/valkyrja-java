/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.grpc.routing.dispatcher;

import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.grpc.message.call.contract.ServiceCallContract;
import io.valkyrja.grpc.message.response.ServiceResponse;
import io.valkyrja.grpc.message.response.contract.ServiceResponseContract;
import io.valkyrja.grpc.middleware.data.RouteMatchedResult;
import io.valkyrja.grpc.middleware.handler.contract.ResponseSentHandlerContract;
import io.valkyrja.grpc.middleware.handler.contract.RouteDispatchedHandlerContract;
import io.valkyrja.grpc.middleware.handler.contract.RouteMatchedHandlerContract;
import io.valkyrja.grpc.middleware.handler.contract.RouteNotMatchedHandlerContract;
import io.valkyrja.grpc.middleware.handler.contract.SendingResponseHandlerContract;
import io.valkyrja.grpc.middleware.handler.contract.ThrowableCaughtHandlerContract;
import io.valkyrja.grpc.routing.collection.contract.RouteCollectionContract;
import io.valkyrja.grpc.routing.data.contract.RouteContract;
import io.valkyrja.grpc.routing.dispatcher.contract.RouterContract;
import io.valkyrja.grpc.support.Cancellation;

/**
 * Resolves an inbound call via a direct service-map lookup and dispatches it through the per-route
 * middleware stages. A missing entry routes to {@code RouteNotMatched} (default terminal: {@code
 * UNIMPLEMENTED}). The two-question cancellation check runs before delegating to {@code
 * RouteMatched} and after the user handler returns, so a cancelled call fast-exits the
 * request-processing stages.
 */
public class Router implements RouterContract {

    protected final ContainerContract container;
    protected final RouteCollectionContract collection;
    protected final RouteMatchedHandlerContract routeMatchedHandler;
    protected final RouteNotMatchedHandlerContract routeNotMatchedHandler;
    protected final RouteDispatchedHandlerContract routeDispatchedHandler;
    protected final ThrowableCaughtHandlerContract throwableCaughtHandler;
    protected final SendingResponseHandlerContract sendingResponseHandler;
    protected final ResponseSentHandlerContract responseSentHandler;

    public Router(
            ContainerContract container,
            RouteCollectionContract collection,
            RouteMatchedHandlerContract routeMatchedHandler,
            RouteNotMatchedHandlerContract routeNotMatchedHandler,
            RouteDispatchedHandlerContract routeDispatchedHandler,
            ThrowableCaughtHandlerContract throwableCaughtHandler,
            SendingResponseHandlerContract sendingResponseHandler,
            ResponseSentHandlerContract responseSentHandler) {
        this.container = container;
        this.collection = collection;
        this.routeMatchedHandler = routeMatchedHandler;
        this.routeNotMatchedHandler = routeNotMatchedHandler;
        this.routeDispatchedHandler = routeDispatchedHandler;
        this.throwableCaughtHandler = throwableCaughtHandler;
        this.sendingResponseHandler = sendingResponseHandler;
        this.responseSentHandler = responseSentHandler;
    }

    @Override
    public ServiceResponseContract dispatch(ServiceCallContract call) {
        String method = call.getMethod();

        if (!collection.has(method)) {
            ServiceResponseContract notFound = ServiceResponse.unimplemented();
            return routeNotMatchedHandler.routeNotMatched(call, notFound);
        }

        return dispatchRoute(call, collection.get(method));
    }

    protected ServiceResponseContract dispatchRoute(ServiceCallContract call, RouteContract route) {
        registerRouteMiddleware(route);

        ServiceCallContract routedCall = call.withRoute(route);
        container.setSingleton(ServiceCallContract.class, routedCall);

        ServiceResponseContract preCheck = Cancellation.checkAndFinalize(routedCall, null);
        if (preCheck != null) {
            return preCheck;
        }

        RouteMatchedResult matched = routeMatchedHandler.routeMatched(routedCall, route);
        if (matched.response() != null) {
            return matched.response();
        }

        RouteContract matchedRoute = matched.route();
        container.setSingleton(RouteContract.class, matchedRoute);

        ServiceResponseContract response = matchedRoute.getHandler().apply(container, matchedRoute);

        ServiceResponseContract postCheck = Cancellation.checkAndFinalize(routedCall, response);
        if (postCheck != null) {
            return postCheck;
        }

        return routeDispatchedHandler.routeDispatched(routedCall, response, matchedRoute);
    }

    @SuppressWarnings("unchecked")
    protected void registerRouteMiddleware(RouteContract route) {
        routeMatchedHandler.add(route.getRouteMatchedMiddleware().toArray(new Class[0]));
        routeDispatchedHandler.add(route.getRouteDispatchedMiddleware().toArray(new Class[0]));
        throwableCaughtHandler.add(route.getThrowableCaughtMiddleware().toArray(new Class[0]));
        sendingResponseHandler.add(route.getSendingResponseMiddleware().toArray(new Class[0]));
        responseSentHandler.add(route.getResponseSentMiddleware().toArray(new Class[0]));
    }
}
