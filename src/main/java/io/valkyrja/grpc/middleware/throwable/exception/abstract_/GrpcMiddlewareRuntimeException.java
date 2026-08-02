/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.grpc.middleware.throwable.exception.abstract_;

import io.valkyrja.grpc.middleware.throwable.contract.GrpcMiddlewareThrowable;
import io.valkyrja.grpc.throwable.exception.abstract_.GrpcRuntimeException;

public abstract class GrpcMiddlewareRuntimeException extends GrpcRuntimeException
        implements GrpcMiddlewareThrowable {

    protected GrpcMiddlewareRuntimeException(String message) {
        super(message);
    }

    protected GrpcMiddlewareRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
