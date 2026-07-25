/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
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
     * Upper bound on the messages buffered for a single call before it is rejected with {@code
     * RESOURCE_EXHAUSTED}. The framework buffers the full inbound stream before dispatching, so
     * this caps memory for an unbounded (e.g. client-streaming) call. Defaults to {@link
     * #DEFAULT_MAX_INBOUND_MESSAGES}; override to raise or lower it.
     *
     * @return the maximum number of inbound messages to buffer per call
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
