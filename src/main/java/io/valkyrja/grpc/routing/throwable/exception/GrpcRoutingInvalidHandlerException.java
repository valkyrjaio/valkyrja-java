/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.grpc.routing.throwable.exception;

import io.valkyrja.grpc.routing.throwable.exception.abstract_.GrpcRoutingInvalidArgumentException;

public class GrpcRoutingInvalidHandlerException extends GrpcRoutingInvalidArgumentException {

    public GrpcRoutingInvalidHandlerException(String message) {
        super(message);
    }

    public GrpcRoutingInvalidHandlerException(String message, Throwable cause) {
        super(message, cause);
    }
}
