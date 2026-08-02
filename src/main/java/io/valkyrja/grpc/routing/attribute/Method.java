/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.grpc.routing.attribute;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method as a gRPC RPC handler. The scan builds a {@code Route} keyed by {@code
 * /service/name} (service coming from the enclosing {@link Service}). The annotated method is
 * invoked as the handler with {@code (ContainerContract, RouteContract)}, returning a {@code
 * ServiceResponse}.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Method {

    /** The RPC method name, e.g. {@code "SayHello"}. */
    String name();

    /** Whether the client streams multiple request messages. */
    boolean clientStreaming() default false;

    /** Whether the server streams multiple response messages. */
    boolean serverStreaming() default false;
}
