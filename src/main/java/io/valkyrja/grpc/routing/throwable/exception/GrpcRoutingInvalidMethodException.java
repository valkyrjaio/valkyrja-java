/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.grpc.routing.throwable.exception;

import io.valkyrja.grpc.routing.throwable.exception.abstract_.GrpcRoutingRuntimeException;

public class GrpcRoutingInvalidMethodException extends GrpcRoutingRuntimeException {

    public GrpcRoutingInvalidMethodException(String message) {
        super(message);
    }

    public GrpcRoutingInvalidMethodException(String message, Throwable cause) {
        super(message, cause);
    }
}
