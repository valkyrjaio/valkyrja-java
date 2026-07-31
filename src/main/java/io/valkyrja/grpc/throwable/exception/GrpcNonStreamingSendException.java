/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
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
