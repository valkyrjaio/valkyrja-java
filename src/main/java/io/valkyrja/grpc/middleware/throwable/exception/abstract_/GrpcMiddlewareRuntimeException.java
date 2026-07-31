/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.grpc.middleware.throwable.exception.abstract_;

import io.valkyrja.grpc.middleware.throwable.contract.GrpcMiddlewareThrowable;
import io.valkyrja.grpc.throwable.exception.abstract_.GrpcRuntimeException;

public abstract class GrpcMiddlewareRuntimeException extends GrpcRuntimeException
        implements GrpcMiddlewareThrowable {

    protected GrpcMiddlewareRuntimeException(String message) {
        super(message);
    }

    protected GrpcMiddlewareRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
