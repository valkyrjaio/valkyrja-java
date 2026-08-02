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
 * Thrown when a streaming handler pushes a message while another push is still in flight. Sends are
 * serialized and the transport is not safe against interleaving, so an overlapping push fails fast
 * rather than silently corrupting the wire framing.
 */
public class GrpcConcurrentSendException extends GrpcRuntimeException {

    public GrpcConcurrentSendException(String message) {
        super(message);
    }
}
