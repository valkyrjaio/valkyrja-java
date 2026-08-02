/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.grpc.server.throwable.exception.abstract_;

import io.valkyrja.grpc.server.throwable.contract.GrpcServerThrowable;
import io.valkyrja.grpc.throwable.exception.abstract_.GrpcInvalidArgumentException;

public abstract class GrpcServerInvalidArgumentException extends GrpcInvalidArgumentException
        implements GrpcServerThrowable {

    protected GrpcServerInvalidArgumentException(String message) {
        super(message);
    }

    protected GrpcServerInvalidArgumentException(String message, Throwable cause) {
        super(message, cause);
    }
}
