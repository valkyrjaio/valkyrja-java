/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.application.data.contract;

import io.valkyrja.grpc.middleware.contract.CallReceivedMiddlewareContract;
import io.valkyrja.grpc.middleware.contract.ResponseSentMiddlewareContract;
import io.valkyrja.grpc.middleware.contract.RouteDispatchedMiddlewareContract;
import io.valkyrja.grpc.middleware.contract.RouteMatchedMiddlewareContract;
import io.valkyrja.grpc.middleware.contract.RouteNotMatchedMiddlewareContract;
import io.valkyrja.grpc.middleware.contract.SendingResponseMiddlewareContract;
import io.valkyrja.grpc.middleware.contract.ThrowableCaughtMiddlewareContract;
import java.util.List;

public interface GrpcConfigContract extends ConfigContract {

    /** Default cap on messages buffered per call before it is rejected. */
    int DEFAULT_MAX_INBOUND_MESSAGES = 1000;

    Integer port();

    /**
     * Upper bound on inbound messages per call. Under the buffered model (unary, server- and
     * client-streaming) it caps the total messages buffered before dispatch, rejecting an
     * over-limit call with {@code RESOURCE_EXHAUSTED}. Under the streaming (bidirectional) model it
     * instead bounds the in-flight window — the high-water mark for flow-control back-pressure, not
     * the total message count — so raising it there raises per-call memory pressure without ever
     * rejecting. Defaults to {@link #DEFAULT_MAX_INBOUND_MESSAGES}; override to raise or lower it.
     *
     * @return the maximum number of inbound messages per call
     */
    default Integer maxInboundMessages() {
        return DEFAULT_MAX_INBOUND_MESSAGES;
    }

    List<Class<? extends CallReceivedMiddlewareContract>> callReceivedMiddleware();

    List<Class<? extends RouteMatchedMiddlewareContract>> routeMatchedMiddleware();

    List<Class<? extends RouteNotMatchedMiddlewareContract>> routeNotMatchedMiddleware();

    List<Class<? extends RouteDispatchedMiddlewareContract>> routeDispatchedMiddleware();

    List<Class<? extends ThrowableCaughtMiddlewareContract>> throwableCaughtMiddleware();

    List<Class<? extends SendingResponseMiddlewareContract>> sendingResponseMiddleware();

    List<Class<? extends ResponseSentMiddlewareContract>> responseSentMiddleware();
}
