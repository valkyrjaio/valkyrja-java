/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.routing.dispatcher;

import io.valkyrja.container.manager.Container;
import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.http.message.enum_.RequestMethod;
import io.valkyrja.http.message.enum_.StatusCode;
import io.valkyrja.http.message.request.contract.ServerRequestContract;
import io.valkyrja.http.message.response.contract.ResponseContract;
import io.valkyrja.http.message.response.factory.ResponseFactory;
import io.valkyrja.http.message.response.factory.contract.ResponseFactoryContract;
import io.valkyrja.http.middleware.data.RouteMatchedResult;
import io.valkyrja.http.middleware.handler.RouteDispatchedHandler;
import io.valkyrja.http.middleware.handler.RouteMatchedHandler;
import io.valkyrja.http.middleware.handler.RouteNotMatchedHandler;
import io.valkyrja.http.middleware.handler.SendingResponseHandler;
import io.valkyrja.http.middleware.handler.TerminatedHandler;
import io.valkyrja.http.middleware.handler.ThrowableCaughtHandler;
import io.valkyrja.http.middleware.handler.contract.RouteDispatchedHandlerContract;
import io.valkyrja.http.middleware.handler.contract.RouteMatchedHandlerContract;
import io.valkyrja.http.middleware.handler.contract.RouteNotMatchedHandlerContract;
import io.valkyrja.http.middleware.handler.contract.SendingResponseHandlerContract;
import io.valkyrja.http.middleware.handler.contract.TerminatedHandlerContract;
import io.valkyrja.http.middleware.handler.contract.ThrowableCaughtHandlerContract;
import io.valkyrja.http.routing.data.contract.RouteContract;
import io.valkyrja.http.routing.dispatcher.contract.RouterContract;
import io.valkyrja.http.routing.matcher.Matcher;
import io.valkyrja.http.routing.matcher.contract.MatcherContract;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class Router implements RouterContract {

    protected ContainerContract container;
    protected MatcherContract matcher;
    protected ResponseFactoryContract responseFactory;
    protected ThrowableCaughtHandlerContract throwableCaughtHandler;
    protected RouteMatchedHandlerContract routeMatchedHandler;
    protected RouteNotMatchedHandlerContract routeNotMatchedHandler;
    protected RouteDispatchedHandlerContract routeDispatchedHandler;
    protected SendingResponseHandlerContract sendingResponseHandler;
    protected TerminatedHandlerContract terminatedHandler;

    public Router() {
        this(new Container(), new Matcher(), new ResponseFactory(),
            new ThrowableCaughtHandler(), new RouteMatchedHandler(), new RouteNotMatchedHandler(),
            new RouteDispatchedHandler(), new SendingResponseHandler(), new TerminatedHandler());
    }

    public Router(
        ContainerContract container,
        MatcherContract matcher,
        ResponseFactoryContract responseFactory,
        ThrowableCaughtHandlerContract throwableCaughtHandler,
        RouteMatchedHandlerContract routeMatchedHandler,
        RouteNotMatchedHandlerContract routeNotMatchedHandler,
        RouteDispatchedHandlerContract routeDispatchedHandler,
        SendingResponseHandlerContract sendingResponseHandler,
        TerminatedHandlerContract terminatedHandler
    ) {
        this.container             = container;
        this.matcher               = matcher;
        this.responseFactory       = responseFactory;
        this.throwableCaughtHandler    = throwableCaughtHandler;
        this.routeMatchedHandler       = routeMatchedHandler;
        this.routeNotMatchedHandler    = routeNotMatchedHandler;
        this.routeDispatchedHandler    = routeDispatchedHandler;
        this.sendingResponseHandler    = sendingResponseHandler;
        this.terminatedHandler         = terminatedHandler;
    }

    @Override
    public ResponseContract dispatch(ServerRequestContract request) {
        Object matchedRoute = attemptToMatchRoute(request);

        if (matchedRoute instanceof ResponseContract response) {
            return routeNotMatchedHandler.routeNotMatched(request, response);
        }

        return dispatchRoute(request, (RouteContract) matchedRoute);
    }

    @Override
    public ResponseContract dispatchRoute(ServerRequestContract request, RouteContract route) {
        routeMatched(route);

        RouteMatchedResult routeAfterMiddlewareResult = routeMatchedHandler.routeMatched(request, route);

        if (routeAfterMiddlewareResult.response() != null) {
            return routeAfterMiddlewareResult.response();
        }

        RouteContract routeAfterMiddleware = routeAfterMiddlewareResult.route();

        container.setSingleton(RouteContract.class, routeAfterMiddleware);

        ResponseContract response = routeAfterMiddleware.getHandler().apply(container, routeAfterMiddleware);

        return routeDispatchedHandler.routeDispatched(request, response, routeAfterMiddleware);
    }

    protected Object attemptToMatchRoute(ServerRequestContract request) {
        String requestPath = URLDecoder.decode(request.getUri().getPath(), StandardCharsets.UTF_8);

        RouteContract route = matcher.match(requestPath, request.getMethod());

        if (route != null) {
            return route;
        }

        if (matcher.match(requestPath, RequestMethod.ANY) != null) {
            return responseFactory.createResponse(null, StatusCode.METHOD_NOT_ALLOWED, null);
        }

        return responseFactory.createResponse(null, StatusCode.NOT_FOUND, null);
    }

    protected void routeMatched(RouteContract route) {
        routeMatchedHandler.add(route.getRouteMatchedMiddleware().toArray(new String[0]));
        routeDispatchedHandler.add(route.getRouteDispatchedMiddleware().toArray(new String[0]));
        throwableCaughtHandler.add(route.getThrowableCaughtMiddleware().toArray(new String[0]));
        sendingResponseHandler.add(route.getSendingResponseMiddleware().toArray(new String[0]));
        terminatedHandler.add(route.getTerminatedMiddleware().toArray(new String[0]));

        container.setSingleton(RouteContract.class, route);
    }
}
