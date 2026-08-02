/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.grpc.middleware.throwable.exception.abstract_;

import io.valkyrja.grpc.middleware.throwable.contract.GrpcMiddlewareThrowable;
import io.valkyrja.grpc.throwable.exception.abstract_.GrpcInvalidArgumentException;

public abstract class GrpcMiddlewareInvalidArgumentException extends GrpcInvalidArgumentException
        implements GrpcMiddlewareThrowable {

    protected GrpcMiddlewareInvalidArgumentException(String message) {
        super(message);
    }

    protected GrpcMiddlewareInvalidArgumentException(String message, Throwable cause) {
        super(message, cause);
    }
}
