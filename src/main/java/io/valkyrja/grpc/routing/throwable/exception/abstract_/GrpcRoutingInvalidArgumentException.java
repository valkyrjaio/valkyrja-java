/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.grpc.routing.throwable.exception.abstract_;

import io.valkyrja.grpc.routing.throwable.contract.GrpcRoutingThrowable;
import io.valkyrja.grpc.throwable.exception.abstract_.GrpcInvalidArgumentException;

public abstract class GrpcRoutingInvalidArgumentException extends GrpcInvalidArgumentException
        implements GrpcRoutingThrowable {

    protected GrpcRoutingInvalidArgumentException(String message) {
        super(message);
    }

    protected GrpcRoutingInvalidArgumentException(String message, Throwable cause) {
        super(message, cause);
    }
}
