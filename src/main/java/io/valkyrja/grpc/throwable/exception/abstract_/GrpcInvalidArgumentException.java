/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.grpc.throwable.exception.abstract_;

import io.valkyrja.grpc.throwable.contract.GrpcThrowable;
import io.valkyrja.throwable.exception.InvalidArgumentException;

public abstract class GrpcInvalidArgumentException extends InvalidArgumentException
        implements GrpcThrowable {

    protected GrpcInvalidArgumentException(String message) {
        super(message);
    }

    protected GrpcInvalidArgumentException(String message, Throwable cause) {
        super(message, cause);
    }
}
