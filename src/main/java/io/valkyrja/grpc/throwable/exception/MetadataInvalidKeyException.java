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
 * Thrown when metadata is added under a key that is not a valid gRPC header name. Raised at the
 * point of insertion — as HTTP does for invalid header names — so a malformed key fails fast in the
 * handler rather than surfacing as an opaque transport error when the response is written.
 */
public class MetadataInvalidKeyException extends GrpcRuntimeException {

    public MetadataInvalidKeyException(String message) {
        super(message);
    }
}
