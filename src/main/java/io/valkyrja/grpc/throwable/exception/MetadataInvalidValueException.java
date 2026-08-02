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
 * Thrown when metadata is added with a value whose type does not match its key's kind — a {@code
 * -bin} key requires {@code byte[]}, every other key requires a {@code String}. Raised at the point
 * of insertion so a mismatch fails fast rather than as a cast failure or a stringified byte array
 * when the response is written.
 */
public class MetadataInvalidValueException extends GrpcRuntimeException {

    public MetadataInvalidValueException(String message) {
        super(message);
    }
}
