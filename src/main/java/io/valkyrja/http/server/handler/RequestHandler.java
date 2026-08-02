/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
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
import io.valkyrja.http.middleware.handler.ResponseSentHandler;
import io.valkyrja.http.middleware.handler.SendingResponseHandler;
import io.valkyrja.http.middleware.handler.ThrowableCaughtHandler;
import io.valkyrja.http.middleware.handler.contract.RequestReceivedHandlerContract;
import io.valkyrja.http.middleware.handler.contract.ResponseSentHandlerContract;
import io.valkyrja.http.middleware.handler.contract.SendingResponseHandlerContract;
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
    protected ResponseSentHandlerContract responseSentHandler;
    protected boolean debug;

    public RequestHandler() {
        this(new Container());
    }

    public RequestHandler(ContainerContract container) {
        this(
                container,
                new Router(container),
                new RequestReceivedHandler(container),
                new ThrowableCaughtHandler(container),
                new SendingResponseHandler(container),
                new ResponseSentHandler(container),
                false);
    }

    public RequestHandler(
            ContainerContract container,
            RouterContract router,
            RequestReceivedHandlerContract requestReceivedHandler,
            ThrowableCaughtHandlerContract throwableCaughtHandler,
            SendingResponseHandlerContract sendingResponseHandler,
            ResponseSentHandlerContract responseSentHandler,
            boolean debug) {
        this.container = container;
        this.router = router;
        this.requestReceivedHandler = requestReceivedHandler;
        this.throwableCaughtHandler = throwableCaughtHandler;
        this.sendingResponseHandler = sendingResponseHandler;
        this.responseSentHandler = responseSentHandler;
        this.debug = debug;
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
        responseSentHandler.responseSent(request, response);
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

        RequestReceivedResult requestAfterMiddleware =
                requestReceivedHandler.requestReceived(request);

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
        return new Response(
                body,
                StatusCode.INTERNAL_SERVER_ERROR,
                new io.valkyrja.http.message.header.collection.HeaderCollection());
    }

    protected ResponseContract getDefaultErrorResponseForHttpException(
            HttpResponseException httpException) {
        StatusCode statusCode = httpException.getStatusCode();
        Stream body = new Stream();
        body.write("Unknown Server Error Occurred - " + httpException.getMessage());
        body.rewind();
        return new Response(
                body,
                statusCode,
                new io.valkyrja.http.message.header.collection.HeaderCollection());
    }
}
