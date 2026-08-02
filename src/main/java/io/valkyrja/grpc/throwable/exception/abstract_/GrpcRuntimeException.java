/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.grpc.throwable.exception.abstract_;

import io.valkyrja.grpc.throwable.contract.GrpcThrowable;
import io.valkyrja.throwable.exception.RuntimeException;

public abstract class GrpcRuntimeException extends RuntimeException implements GrpcThrowable {

    protected GrpcRuntimeException(String message) {
        super(message);
    }

    protected GrpcRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
