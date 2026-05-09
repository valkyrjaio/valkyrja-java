/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.server.handler;

import io.valkyrja.container.manager.Container;
import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.http.message.enum_.StatusCode;
import io.valkyrja.http.message.request.contract.ServerRequestContract;
import io.valkyrja.http.message.response.Response;
import io.valkyrja.http.message.response.contract.ResponseContract;
import io.valkyrja.http.message.stream.Stream;
import io.valkyrja.http.message.throwable.exception.HttpResponseException;
import io.valkyrja.http.middleware.data.RequestReceivedResult;
import io.valkyrja.http.middleware.handler.RequestReceivedHandler;
import io.valkyrja.http.middleware.handler.SendingResponseHandler;
import io.valkyrja.http.middleware.handler.TerminatedHandler;
import io.valkyrja.http.middleware.handler.ThrowableCaughtHandler;
import io.valkyrja.http.middleware.handler.contract.RequestReceivedHandlerContract;
import io.valkyrja.http.middleware.handler.contract.SendingResponseHandlerContract;
import io.valkyrja.http.middleware.handler.contract.TerminatedHandlerContract;
import io.valkyrja.http.middleware.handler.contract.ThrowableCaughtHandlerContract;
import io.valkyrja.http.routing.dispatcher.Router;
import io.valkyrja.http.routing.dispatcher.contract.RouterContract;
import io.valkyrja.http.server.handler.contract.RequestHandlerContract;

public class RequestHandler implements RequestHandlerContract {

    protected ContainerContract container;
    protected RouterContract router;
    protected RequestReceivedHandlerContract requestReceivedHandler;
    protected ThrowableCaughtHandlerContract throwableCaughtHandler;
    protected SendingResponseHandlerContract sendingResponseHandler;
    protected TerminatedHandlerContract terminatedHandler;
    protected boolean debug;

    public RequestHandler() {
        this(new Container(), new Router(), new RequestReceivedHandler(),
            new ThrowableCaughtHandler(), new SendingResponseHandler(), new TerminatedHandler(), false);
    }

    public RequestHandler(
        ContainerContract container,
        RouterContract router,
        RequestReceivedHandlerContract requestReceivedHandler,
        ThrowableCaughtHandlerContract throwableCaughtHandler,
        SendingResponseHandlerContract sendingResponseHandler,
        TerminatedHandlerContract terminatedHandler,
        boolean debug
    ) {
        this.container               = container;
        this.router                  = router;
        this.requestReceivedHandler  = requestReceivedHandler;
        this.throwableCaughtHandler  = throwableCaughtHandler;
        this.sendingResponseHandler  = sendingResponseHandler;
        this.terminatedHandler       = terminatedHandler;
        this.debug                   = debug;
    }

    @Override
    public ResponseContract handle(ServerRequestContract request) {
        ResponseContract response;
        try {
            response = dispatchRouter(request);
        } catch (Throwable throwable) {
            response = getResponseFromThrowable(throwable);
            response = throwableCaughtHandler.throwableCaught(request, response, throwable);
        }

        container.setSingleton(ResponseContract.class, response);

        return response;
    }

    @Override
    public RequestHandlerContract send(ResponseContract response) {
        response.send();
        return this;
    }

    @Override
    public void terminate(ServerRequestContract request, ResponseContract response) {
        terminatedHandler.terminated(request, response);
    }

    @Override
    public void run(ServerRequestContract request) {
        ResponseContract response = handle(request);
        response = sendingResponseHandler.sendingResponse(request, response);

        container.setSingleton(ResponseContract.class, response);

        send(response);
        terminate(request, response);
    }

    protected ResponseContract dispatchRouter(ServerRequestContract request) {
        container.setSingleton(ServerRequestContract.class, request);

        RequestReceivedResult requestAfterMiddleware = requestReceivedHandler.requestReceived(request);

        if (requestAfterMiddleware.response() != null) {
            return requestAfterMiddleware.response();
        }

        ServerRequestContract updatedRequest = requestAfterMiddleware.request();
        container.setSingleton(ServerRequestContract.class, updatedRequest);

        return router.dispatch(updatedRequest);
    }

    protected ResponseContract getResponseFromThrowable(Throwable throwable) {
        if (debug) {
            throw new RuntimeException(throwable);
        }

        if (throwable instanceof HttpResponseException httpEx) {
            ResponseContract response = httpEx.getResponse();
            return response != null ? response : getDefaultErrorResponseForHttpException(httpEx);
        }

        return getDefaultErrorResponse();
    }

    protected ResponseContract getDefaultErrorResponse() {
        Stream body = new Stream();
        body.write("Unknown Server Error Occurred");
        body.rewind();
        return new Response(body, StatusCode.INTERNAL_SERVER_ERROR, new io.valkyrja.http.message.header.collection.HeaderCollection());
    }

    protected ResponseContract getDefaultErrorResponseForHttpException(HttpResponseException httpException) {
        StatusCode statusCode = httpException.getStatusCode();
        Stream body = new Stream();
        body.write("Unknown Server Error Occurred - " + httpException.getMessage());
        body.rewind();
        return new Response(body, statusCode, new io.valkyrja.http.message.header.collection.HeaderCollection());
    }
}