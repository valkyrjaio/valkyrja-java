/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.fixtures.http.middleware;

import io.valkyrja.http.message.request.contract.ServerRequestContract;
import io.valkyrja.http.message.response.contract.ResponseContract;
import io.valkyrja.http.middleware.contract.RequestReceivedMiddlewareContract;
import io.valkyrja.http.middleware.contract.ResponseSentMiddlewareContract;
import io.valkyrja.http.middleware.contract.RouteDispatchedMiddlewareContract;
import io.valkyrja.http.middleware.contract.RouteMatchedMiddlewareContract;
import io.valkyrja.http.middleware.contract.RouteNotMatchedMiddlewareContract;
import io.valkyrja.http.middleware.contract.SendingResponseMiddlewareContract;
import io.valkyrja.http.middleware.contract.ThrowableCaughtMiddlewareContract;
import io.valkyrja.http.middleware.data.RequestReceivedResult;
import io.valkyrja.http.middleware.data.RouteMatchedResult;
import io.valkyrja.http.middleware.handler.contract.RequestReceivedHandlerContract;
import io.valkyrja.http.middleware.handler.contract.ResponseSentHandlerContract;
import io.valkyrja.http.middleware.handler.contract.RouteDispatchedHandlerContract;
import io.valkyrja.http.middleware.handler.contract.RouteMatchedHandlerContract;
import io.valkyrja.http.middleware.handler.contract.RouteNotMatchedHandlerContract;
import io.valkyrja.http.middleware.handler.contract.SendingResponseHandlerContract;
import io.valkyrja.http.middleware.handler.contract.ThrowableCaughtHandlerContract;
import io.valkyrja.http.routing.data.contract.RouteContract;

/** An http middleware implementing every routing contract, delegating back to the next handler. */
public final class PassThroughHttpMiddleware
        implements RequestReceivedMiddlewareContract,
                RouteMatchedMiddlewareContract,
                RouteNotMatchedMiddlewareContract,
                RouteDispatchedMiddlewareContract,
                ThrowableCaughtMiddlewareContract,
                SendingResponseMiddlewareContract,
                ResponseSentMiddlewareContract {

    @Override
    public RequestReceivedResult requestReceived(
            ServerRequestContract request, RequestReceivedHandlerContract handler) {
        return handler.requestReceived(request);
    }

    @Override
    public ResponseContract routeNotMatched(
            ServerRequestContract request,
            ResponseContract response,
            RouteNotMatchedHandlerContract handler) {
        return handler.routeNotMatched(request, response);
    }

    @Override
    public RouteMatchedResult routeMatched(
            ServerRequestContract request,
            RouteContract route,
            RouteMatchedHandlerContract handler) {
        return handler.routeMatched(request, route);
    }

    @Override
    public ResponseContract routeDispatched(
            ServerRequestContract request,
            ResponseContract response,
            RouteContract route,
            RouteDispatchedHandlerContract handler) {
        return handler.routeDispatched(request, response, route);
    }

    @Override
    public ResponseContract throwableCaught(
            ServerRequestContract request,
            ResponseContract response,
            Throwable throwable,
            ThrowableCaughtHandlerContract handler) {
        return handler.throwableCaught(request, response, throwable);
    }

    @Override
    public ResponseContract sendingResponse(
            ServerRequestContract request,
            ResponseContract response,
            SendingResponseHandlerContract handler) {
        return handler.sendingResponse(request, response);
    }

    @Override
    public void responseSent(
            ServerRequestContract request,
            ResponseContract response,
            ResponseSentHandlerContract handler) {
        handler.responseSent(request, response);
    }
}
