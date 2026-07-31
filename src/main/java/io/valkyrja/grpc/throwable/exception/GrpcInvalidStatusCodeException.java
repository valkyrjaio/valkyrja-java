/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.grpc.throwable.exception;

import io.valkyrja.grpc.throwable.exception.abstract_.GrpcInvalidArgumentException;

/**
 * Thrown when an integer taken off the wire does not name a gRPC status code. The {@code
 * grpc-status} trailer carries 0–16; anything else is a malformed peer, not a status this framework
 * can act on.
 */
public class GrpcInvalidStatusCodeException extends GrpcInvalidArgumentException {

    public GrpcInvalidStatusCodeException(String message) {
        super(message);
    }
}
