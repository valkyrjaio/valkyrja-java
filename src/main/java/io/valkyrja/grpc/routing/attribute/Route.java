/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.grpc.routing.attribute;

import io.valkyrja.grpc.middleware.contract.ResponseSentMiddlewareContract;
import io.valkyrja.grpc.middleware.contract.RouteDispatchedMiddlewareContract;
import io.valkyrja.grpc.middleware.contract.RouteMatchedMiddlewareContract;
import io.valkyrja.grpc.middleware.contract.SendingResponseMiddlewareContract;
import io.valkyrja.grpc.middleware.contract.ThrowableCaughtMiddlewareContract;
import io.valkyrja.grpc.routing.attribute.route.Handler;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Route {

    /** The RPC method name, e.g. {@code "SayHello"}. */
    String name();

    /**
     * The handler, as the class and the method name. {@code Void.class} means the route names no
     * handler here, so the method-level {@link Handler} or the annotated method itself
     * supplies one.
     */
    Handler handler() default @Handler(handlerClass = Void.class, handlerMethod = "");

    /** The generated protobuf request message type. {@code Void.class} means unspecified. */
    Class<?> requestType() default Void.class;

    /** The generated protobuf response message type. {@code Void.class} means unspecified. */
    Class<?> responseType() default Void.class;

    /** Whether the client streams multiple request messages. */
    boolean clientStreaming() default false;

    /** Whether the server streams multiple response messages. */
    boolean serverStreaming() default false;

    Class<? extends RouteMatchedMiddlewareContract>[] routeMatchedMiddleware() default {};

    Class<? extends RouteDispatchedMiddlewareContract>[] routeDispatchedMiddleware() default {};

    Class<? extends ThrowableCaughtMiddlewareContract>[] throwableCaughtMiddleware() default {};

    Class<? extends SendingResponseMiddlewareContract>[] sendingResponseMiddleware() default {};

    Class<? extends ResponseSentMiddlewareContract>[] responseSentMiddleware() default {};
}
