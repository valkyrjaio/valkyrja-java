/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.grpc.routing.throwable.exception.abstract_;

import io.valkyrja.grpc.routing.throwable.contract.GrpcRoutingThrowable;
import io.valkyrja.grpc.throwable.exception.abstract_.GrpcRuntimeException;

public abstract class GrpcRoutingRuntimeException extends GrpcRuntimeException
        implements GrpcRoutingThrowable {

    protected GrpcRoutingRuntimeException(String message) {
        super(message);
    }

    protected GrpcRoutingRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
