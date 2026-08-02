/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.grpc.throwable.exception;

import io.valkyrja.grpc.throwable.exception.abstract_.GrpcRuntimeException;

/**
 * Thrown when a handler pushes a message on a buffered call. The push sink exists only under the
 * streaming model; a buffered call returns its messages on the {@code ServiceResponse} instead.
 */
public class GrpcNonStreamingSendException extends GrpcRuntimeException {

    public GrpcNonStreamingSendException(String message) {
        super(message);
    }
}
